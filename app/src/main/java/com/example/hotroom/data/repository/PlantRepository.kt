package com.example.hotroom.data.repository

import com.example.hotroom.data.SupabaseClient
import com.example.hotroom.data.model.Plant
import com.example.hotroom.data.model.WateringLog
import io.github.jan.supabase.postgrest.postgrest

class PlantRepository {

    private val postgrest = SupabaseClient.client.postgrest

    suspend fun getPlants(userId: String): Result<List<Plant>> {
        return try {
            val plants = postgrest.from("plants")
                .select() {
                    filter { eq("user_id", userId) }
                }
                .decodeList<Plant>()
            Result.success(plants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlantById(plantId: String): Result<Plant> {
        return try {
            val plant = postgrest.from("plants")
                .select() {
                    filter { eq("id", plantId) }
                }
                .decodeSingle<Plant>()
            Result.success(plant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlantsByZone(userId: String, zone: String): Result<List<Plant>> {
        return try {
            val plants = postgrest.from("plants")
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("zone", zone)
                    }
                }
                .decodeList<Plant>()
            Result.success(plants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addPlant(plant: Plant): Result<Unit> {
        return try {
            postgrest.from("plants").insert(plant)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePlant(plant: Plant): Result<Unit> {
        return try {
            postgrest.from("plants").update(plant) {
                filter { eq("id", plant.id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sug'orish — watering_logs ga log yozadi.
     * DB trigger avtomatik: health +5, last_watered = now()
     */
    suspend fun waterPlant(plantId: String, userId: String, greenhouseId: String? = null): Result<Unit> {
        return try {
            val log = WateringLog(
                plantId = plantId,
                userId = userId,
                greenhouseId = greenhouseId ?: SessionManager.greenhouseId,
                completedBy = userId // currently treating userId as the one who completed
            )
            postgrest.from("watering_logs").insert(log)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWateringHistory(plantId: String): Result<List<WateringLog>> {
        return try {
            val logs = postgrest.from("watering_logs")
                .select() {
                    filter { eq("plant_id", plantId) }
                }
                .decodeList<WateringLog>()
            Result.success(logs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePlant(plantId: String): Result<Unit> {
        return try {
            postgrest.from("plants").delete {
                filter { eq("id", plantId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchPlants(userId: String, query: String): Result<List<Plant>> {
        return try {
            val plants = postgrest.from("plants")
                .select() {
                    filter {
                        eq("user_id", userId)
                        ilike("name", "%$query%")
                    }
                }
                .decodeList<Plant>()
            Result.success(plants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sug'orish muddati o'tgan o'simliklarni topish
     */
    suspend fun getPlantsNeedingWater(userId: String): Result<List<Plant>> {
        return try {
            val plants = postgrest.from("plants")
                .select() {
                    filter { eq("user_id", userId) }
                }
                .decodeList<Plant>()

            val now = java.time.Instant.now()
            val needWatering = plants.filter { plant ->
                if (plant.lastWatered == null) return@filter true
                try {
                    val lastWatered = java.time.Instant.parse(plant.lastWatered)
                    val hoursSince = java.time.Duration.between(lastWatered, now).toHours()
                    hoursSince >= plant.wateringIntervalHours
                } catch (e: Exception) {
                    true
                }
            }
            Result.success(needWatering)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
