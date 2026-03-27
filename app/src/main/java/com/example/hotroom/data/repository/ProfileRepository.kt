package com.example.hotroom.data.repository

import com.example.hotroom.data.SupabaseClient
import com.example.hotroom.data.model.Profile
import io.github.jan.supabase.postgrest.postgrest

class ProfileRepository {

    private val postgrest = SupabaseClient.client.postgrest

    suspend fun getProfile(userId: String): Result<Profile> {
        return try {
            val profile = postgrest.from("profiles")
                .select() {
                    filter { eq("id", userId) }
                }
                .decodeSingle<Profile>()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(userId: String, updates: Map<String, Any?>): Result<Unit> {
        return try {
            postgrest.from("profiles").update(updates) {
                filter { eq("id", userId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNotificationSettings(
        userId: String,
        notifyWatering: Boolean,
        notifyTemperature: Boolean
    ): Result<Unit> {
        return try {
            postgrest.from("profiles").update(
                mapOf(
                    "notify_watering" to notifyWatering,
                    "notify_temperature" to notifyTemperature
                )
            ) {
                filter { eq("id", userId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
