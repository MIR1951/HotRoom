package com.example.hotroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotroom.data.model.Plant
import com.example.hotroom.data.model.SensorReading
import com.example.hotroom.data.repository.AuthRepository
import com.example.hotroom.data.repository.PlantRepository
import com.example.hotroom.data.repository.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SensorUiState(
    val isLoading: Boolean = false,
    val latestReadings: List<SensorReading> = emptyList(),
    val zoneReadings: Map<String, SensorReading> = emptyMap(),
    val currentTemperature: Float? = null,
    val currentHumidity: Float? = null,
    val temperatureHistory: List<Float> = emptyList(),
    val humidityHistory: List<Float> = emptyList(),
    val plants: List<Plant> = emptyList(),
    val selectedPlantId: String? = null,
    val errorMessage: String? = null
)

class SensorViewModel(
    private val sensorRepository: SensorRepository = SensorRepository(),
    private val plantRepository: PlantRepository = PlantRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SensorUiState())
    val uiState: StateFlow<SensorUiState> = _uiState.asStateFlow()

    init {
        loadSensorData()
    }

    fun loadSensorData(showLoading: Boolean = true) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            // Oxirgi ko'rsatkichlar
            val readingsResult = sensorRepository.getLatestReadings(userId)
            val readings = readingsResult.getOrDefault(emptyList())

            // Zonalar bo'yicha
            val zonesResult = sensorRepository.getLatestByZones(userId)
            val zones = zonesResult.getOrDefault(emptyMap())

            // O'simliklar
            val plantsResult = plantRepository.getPlants(userId)
            val plants = plantsResult.getOrDefault(emptyList())

            // Harorat va namlik tarixi
            val tempHistory = readings.mapNotNull { it.temperature }.reversed()
            val humidityHistory = readings.mapNotNull { it.humidity }.reversed()

            // Joriy qiymatlar
            val currentTemp = readings.firstOrNull()?.temperature
            val currentHumidity = readings.firstOrNull()?.humidity

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                latestReadings = readings,
                zoneReadings = zones,
                currentTemperature = currentTemp,
                currentHumidity = currentHumidity,
                temperatureHistory = tempHistory,
                humidityHistory = humidityHistory,
                plants = plants
            )
        }
    }

    fun selectPlant(plantId: String?) {
        _uiState.value = _uiState.value.copy(selectedPlantId = plantId)
        if (plantId == null) {
            loadSensorData(showLoading = true)
        } else {
            loadPlantSensorData(plantId, showLoading = true)
        }
    }

    private fun loadPlantSensorData(plantId: String, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }
            
            // Muayyan o'simlik bo'yicha tarix
            val readingResult = sensorRepository.getReadingsForPlant(plantId)
            val readings = readingResult.getOrDefault(emptyList())

            val tempHistory = readings.mapNotNull { it.temperature }.reversed()
            val humidityHistory = readings.mapNotNull { it.humidity }.reversed()

            val currentTemp = readings.firstOrNull()?.temperature
            val currentHumidity = readings.firstOrNull()?.humidity

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                currentTemperature = currentTemp ?: _uiState.value.currentTemperature,
                currentHumidity = currentHumidity ?: _uiState.value.currentHumidity,
                temperatureHistory = if (tempHistory.isNotEmpty()) tempHistory else _uiState.value.temperatureHistory,
                humidityHistory = if (humidityHistory.isNotEmpty()) humidityHistory else _uiState.value.humidityHistory
            )
        }
    }
}
