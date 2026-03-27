package com.example.hotroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotroom.data.model.Plant
import com.example.hotroom.data.repository.AuthRepository
import com.example.hotroom.data.repository.PlantRepository
import com.example.hotroom.data.repository.TaskRepository
import com.example.hotroom.data.model.CareTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PlantUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val isAddingPlant: Boolean = false,
    val addPlantSuccess: Boolean = false
)

class PlantViewModel(
    private val plantRepository: PlantRepository = PlantRepository(),
    private val taskRepository: TaskRepository = TaskRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantUiState())
    val uiState: StateFlow<PlantUiState> = _uiState.asStateFlow()

    // Boshqa ViewModel'larni xabardor qilish uchun
    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger.asStateFlow()

    init {
        loadPlants()
    }

    fun loadPlants() {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = plantRepository.getPlants(userId)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    plants = result.getOrDefault(emptyList()),
                    isLoading = false
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun searchPlants(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            val result = if (query.isBlank()) {
                plantRepository.getPlants(userId)
            } else {
                plantRepository.searchPlants(userId, query)
            }
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    plants = result.getOrDefault(emptyList())
                )
            }
        }
    }

    fun addPlant(
        name: String,
        scientificName: String?,
        category: String,
        plantedDate: String?,
        hasSensor: Boolean,
        wateringIntervalHours: Int,
        zone: String?,
        notes: String?
    ) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingPlant = true, addPlantSuccess = false)
            val plant = Plant(
                userId = userId,
                name = name,
                scientificName = scientificName?.ifBlank { null },
                category = category.lowercase(),
                plantedDate = plantedDate,
                hasSensor = hasSensor,
                wateringIntervalHours = wateringIntervalHours,
                zone = zone?.ifBlank { null },
                notes = notes?.ifBlank { null }
            )
            val result = plantRepository.addPlant(plant)
            if (result.isSuccess) {
                // Avtomatik sug'orish vazifasi yaratish
                val task = CareTask(
                    userId = userId,
                    title = "$name ni sug'orish",
                    taskType = "watering",
                    scheduledDate = LocalDate.now().toString(),
                    scheduledTime = "08:00",
                    description = "Avtomatik yaratilgan vazifa"
                )
                taskRepository.addTask(task)

                _uiState.value = _uiState.value.copy(isAddingPlant = false, addPlantSuccess = true)
                loadPlants()
                notifyRefresh()
            } else {
                _uiState.value = _uiState.value.copy(
                    isAddingPlant = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun deletePlant(plantId: String) {
        viewModelScope.launch {
            plantRepository.deletePlant(plantId)
            loadPlants()
            notifyRefresh()
        }
    }

    /**
     * Sug'orish: watering_log yaratish + bugungi sug'orish vazifalarini bajarildi qilish
     */
    fun waterPlant(plantId: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            // 1. Watering log yozish (trigger health yangilaydi)
            plantRepository.waterPlant(plantId, userId)
            // 2. Bugungi sug'orish vazifalarini avtomatik bajarildi qilish
            taskRepository.completeWateringTasksForPlant(plantId, LocalDate.now().toString())
            // 3. Ro'yxatni yangilash
            loadPlants()
            notifyRefresh()
        }
    }

    fun resetAddPlantSuccess() {
        _uiState.value = _uiState.value.copy(addPlantSuccess = false)
    }

    private fun notifyRefresh() {
        _refreshTrigger.value++
    }
}
