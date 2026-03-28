package com.example.hotroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotroom.data.model.CareTask
import com.example.hotroom.data.model.Plant
import com.example.hotroom.data.model.SensorReading
import com.example.hotroom.data.repository.AuthRepository
import com.example.hotroom.data.repository.PlantRepository
import com.example.hotroom.data.repository.SensorRepository
import com.example.hotroom.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val isLoading: Boolean = false,
    val plants: List<Plant> = emptyList(),
    val plantsNeedingWater: List<Plant> = emptyList(),
    val todayTasks: List<CareTask> = emptyList(),
    val latestReading: SensorReading? = null,
    val averageHealth: Int = 0,
    val totalPlants: Int = 0,
    val completedTasksToday: Int = 0,
    val totalTasksToday: Int = 0,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val plantRepository: PlantRepository = PlantRepository(),
    private val taskRepository: TaskRepository = TaskRepository(),
    private val sensorRepository: SensorRepository = SensorRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard(showLoading: Boolean = true) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            // O'simliklarni olish
            val plantsResult = plantRepository.getPlants(userId)
            val plants = plantsResult.getOrDefault(emptyList())

            // Sug'orish kerak o'simliklar
            val needWaterResult = plantRepository.getPlantsNeedingWater(userId)
            val needWater = needWaterResult.getOrDefault(emptyList())

            // Bugungi vazifalarni olish
            val today = LocalDate.now().toString()
            val tasksResult = taskRepository.getTodayTasks(userId, today)
            val tasks = tasksResult.getOrDefault(emptyList()).map { task ->
                if (task.isCompleted && task.completedBy != null) {
                    task.copy(completerName = com.example.hotroom.data.repository.SessionManager.greenhouseProfiles[task.completedBy]?.name?.ifBlank { "Ishchi" })
                } else task
            }

            // Oxirgi sensor ko'rsatkichini olish
            val sensorResult = sensorRepository.getLatestReadings(userId)
            val latestReading = sensorResult.getOrDefault(emptyList()).firstOrNull()

            // O'rtacha health hisoblash
            val avgHealth = if (plants.isNotEmpty()) {
                plants.map { it.healthStatus }.average().toInt()
            } else 0

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                plants = plants,
                plantsNeedingWater = needWater,
                todayTasks = tasks,
                latestReading = latestReading,
                averageHealth = avgHealth,
                totalPlants = plants.size,
                completedTasksToday = tasks.count { it.isCompleted },
                totalTasksToday = tasks.size
            )
        }
    }

    fun markTaskComplete(taskId: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            authRepository.ensureProfileLoaded()
            taskRepository.markComplete(taskId, userId)
            loadDashboard(showLoading = false)
        }
    }
}
