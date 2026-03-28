package com.example.hotroom.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String = "",
    val name: String = "",
    val role: String = "admin",
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("greenhouse_location") val greenhouseLocation: String? = null,
    @SerialName("greenhouse_type") val greenhouseType: String? = null,
    @SerialName("greenhouse_area") val greenhouseArea: Float? = null,
    @SerialName("notify_watering") val notifyWatering: Boolean = true,
    @SerialName("notify_temperature") val notifyTemperature: Boolean = true,
    @SerialName("greenhouse_id") val greenhouseId: String? = null,
    @SerialName("theme_preference") val themePreference: String? = null,
    @SerialName("language_preference") val languagePreference: String? = "uz"
)

@Serializable
data class Plant(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("greenhouse_id") val greenhouseId: String? = null,
    val name: String = "",
    @SerialName("scientific_name") val scientificName: String? = null,
    val category: String = "sabzavot",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("health_status") val healthStatus: Int = 100,
    @SerialName("planted_date") val plantedDate: String? = null,
    @SerialName("last_watered") val lastWatered: String? = null,
    @SerialName("has_sensor") val hasSensor: Boolean = false,
    @SerialName("watering_interval_hours") val wateringIntervalHours: Int = 24,
    @SerialName("light_requirement") val lightRequirement: String? = null,
    @SerialName("humidity_requirement") val humidityRequirement: String? = null,
    val zone: String? = null,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CareTask(
    val id: String = "",
    @SerialName("plant_id") val plantId: String? = null,
    @SerialName("user_id") val userId: String = "",
    @SerialName("greenhouse_id") val greenhouseId: String? = null,
    val title: String = "",
    val description: String? = null,
    @SerialName("task_type") val taskType: String = "watering",
    @SerialName("scheduled_date") val scheduledDate: String = "",
    @SerialName("scheduled_time") val scheduledTime: String? = null,
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("completed_by") val completedBy: String? = null,
    
    // UI uchun shunchaki saqlanadigan xususiyat (Bazada bunday column yo'q)
    var completerName: String? = null
)

@Serializable
data class SensorReading(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("plant_id") val plantId: String? = null,
    @SerialName("greenhouse_id") val greenhouseId: String? = null,
    val zone: String = "",
    val temperature: Float? = null,
    val humidity: Float? = null,
    @SerialName("light_level") val lightLevel: Float? = null,
    @SerialName("ph_level") val phLevel: Float? = null,
    @SerialName("co2_level") val co2Level: Int? = null,
    @SerialName("vpd_level") val vpdLevel: Float? = null,
    @SerialName("recorded_at") val recordedAt: String? = null
)

@Serializable
data class WateringLog(
    val id: String = "",
    @SerialName("plant_id") val plantId: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("greenhouse_id") val greenhouseId: String? = null,
    @SerialName("watered_at") val wateredAt: String? = null,
    val notes: String? = null,
    @SerialName("completed_by") val completedBy: String? = null
)
