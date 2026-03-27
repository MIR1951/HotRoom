package com.example.hotroom.data.repository

import com.example.hotroom.data.SupabaseClient
import com.example.hotroom.data.model.SensorReading
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class SensorRepository {

    private val postgrest = SupabaseClient.client.postgrest

    suspend fun getLatestReadings(userId: String): Result<List<SensorReading>> {
        return try {
            val readings = postgrest.from("sensor_readings")
                .select() {
                    filter { eq("user_id", userId) }
                    order("recorded_at", Order.DESCENDING)
                    limit(10)
                }
                .decodeList<SensorReading>()
            Result.success(readings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReadingsByZone(userId: String, zone: String): Result<List<SensorReading>> {
        return try {
            val readings = postgrest.from("sensor_readings")
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("zone", zone)
                    }
                    order("recorded_at", Order.DESCENDING)
                    limit(24)
                }
                .decodeList<SensorReading>()
            Result.success(readings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReadingsForPlant(plantId: String): Result<List<SensorReading>> {
        return try {
            val readings = postgrest.from("sensor_readings")
                .select() {
                    filter { eq("plant_id", plantId) }
                    order("recorded_at", Order.DESCENDING)
                    limit(24)
                }
                .decodeList<SensorReading>()
            Result.success(readings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLatestByZones(userId: String): Result<Map<String, SensorReading>> {
        return try {
            val readings = postgrest.from("sensor_readings")
                .select() {
                    filter { eq("user_id", userId) }
                    order("recorded_at", Order.DESCENDING)
                }
                .decodeList<SensorReading>()

            val zoneMap = readings.groupBy { it.zone }
                .mapValues { (_, readings) -> readings.first() }
            Result.success(zoneMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addReading(reading: SensorReading): Result<Unit> {
        return try {
            postgrest.from("sensor_readings").insert(reading)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
