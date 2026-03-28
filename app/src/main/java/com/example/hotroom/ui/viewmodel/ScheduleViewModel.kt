package com.example.hotroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotroom.data.model.CareTask
import com.example.hotroom.data.model.Plant
import com.example.hotroom.data.repository.AuthRepository
import com.example.hotroom.data.repository.PlantRepository
import com.example.hotroom.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import com.example.hotroom.data.repository.SessionManager

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val tasks: List<CareTask> = emptyList(),
    val plants: List<Plant> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val taskDays: Set<Int> = emptySet(),
    val errorMessage: String? = null,
    val isAddingTask: Boolean = false,
    val addTaskSuccess: Boolean = false
)

class ScheduleViewModel(
    private val taskRepository: TaskRepository = TaskRepository(),
    private val plantRepository: PlantRepository = PlantRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadPlants()
        loadMonthTasks()
    }

    private fun loadPlants() {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            val result = plantRepository.getPlants(userId)
            _uiState.value = _uiState.value.copy(
                plants = result.getOrDefault(emptyList())
            )
        }
    }

    fun loadMonthTasks(showLoading: Boolean = true) {
        val userId = authRepository.getCurrentUserId() ?: return
        val currentMonth = _uiState.value.currentMonth
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }
            val result = taskRepository.getTasksByMonth(
                userId, currentMonth.year, currentMonth.monthValue
            )
            val allTasks = result.getOrDefault(emptyList())

            val days = allTasks.mapNotNull {
                try {
                    LocalDate.parse(it.scheduledDate).dayOfMonth
                } catch (e: Exception) { null }
            }.toSet()

            val selectedDate = _uiState.value.selectedDate
            val dayTasks = allTasks.filter {
                it.scheduledDate == selectedDate.toString()
            }.map { task ->
                if (task.isCompleted && task.completedBy != null) {
                    task.copy(completerName = com.example.hotroom.data.repository.SessionManager.greenhouseProfiles[task.completedBy]?.name?.ifBlank { "Ishchi" })
                } else task
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                tasks = dayTasks,
                taskDays = days
            )
        }
    }

    fun selectDate(day: Int) {
        val currentMonth = _uiState.value.currentMonth
        val newDate = LocalDate.of(currentMonth.year, currentMonth.monthValue, day)
        _uiState.value = _uiState.value.copy(selectedDate = newDate)
        loadTasksForSelectedDate()
    }

    private fun loadTasksForSelectedDate() {
        val userId = authRepository.getCurrentUserId() ?: return
        val selectedDate = _uiState.value.selectedDate
        viewModelScope.launch {
            val result = taskRepository.getTodayTasks(userId, selectedDate.toString())
            _uiState.value = _uiState.value.copy(
                tasks = result.getOrDefault(emptyList()).map { task ->
                    if (task.isCompleted && task.completedBy != null) {
                        task.copy(completerName = com.example.hotroom.data.repository.SessionManager.greenhouseProfiles[task.completedBy]?.name?.ifBlank { "Ishchi" })
                    } else task
                }
            )
        }
    }

    fun previousMonth() {
        _uiState.value = _uiState.value.copy(
            currentMonth = _uiState.value.currentMonth.minusMonths(1)
        )
        loadMonthTasks()
    }

    fun nextMonth() {
        _uiState.value = _uiState.value.copy(
            currentMonth = _uiState.value.currentMonth.plusMonths(1)
        )
        loadMonthTasks()
    }

    /**
     * Vazifa qo'shish — o'simlikka bog'langan
     */
    fun addTask(title: String, taskType: String, date: LocalDate, time: String?, plantId: String?) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            authRepository.ensureProfileLoaded()
            _uiState.value = _uiState.value.copy(isAddingTask = true)
            val task = CareTask(
                userId = userId,
                greenhouseId = SessionManager.greenhouseId,
                plantId = plantId,
                title = title,
                taskType = taskType,
                scheduledDate = date.toString(),
                scheduledTime = time
            )
            val result = taskRepository.addTask(task)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isAddingTask = false, addTaskSuccess = true)
            } else {
                _uiState.value.copy(
                    isAddingTask = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
            if (result.isSuccess) {
                loadMonthTasks(showLoading = false)
            }
        }
    }

    fun markComplete(taskId: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            authRepository.ensureProfileLoaded()
            taskRepository.markComplete(taskId, userId)
            loadTasksForSelectedDate()
            loadMonthTasks(showLoading = false)
        }
    }

    fun resetAddTaskSuccess() {
        _uiState.value = _uiState.value.copy(addTaskSuccess = false)
    }

    /**
     * O'simlik nomini ID bo'yicha topish
     */
    fun getPlantName(plantId: String?): String? {
        if (plantId == null) return null
        return _uiState.value.plants.find { it.id == plantId }?.name
    }
}
