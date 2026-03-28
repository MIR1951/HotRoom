package com.example.hotroom.data.repository

import com.example.hotroom.data.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {

    private val auth = SupabaseClient.client.auth

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, name: String): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = kotlinx.serialization.json.buildJsonObject {
                    put("name", kotlinx.serialization.json.JsonPrimitive(name))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    val sessionStatus: kotlinx.coroutines.flow.StateFlow<io.github.jan.supabase.auth.status.SessionStatus>
        get() = auth.sessionStatus

    fun isLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUserOrNull()?.email
    }

    suspend fun ensureProfileLoaded(profileRepo: ProfileRepository = ProfileRepository()) {
        if (SessionManager.currentProfile == null) {
            val uid = getCurrentUserId() ?: return
            val profile = profileRepo.getProfile(uid).getOrNull()
            SessionManager.currentProfile = profile
            
            profile?.greenhouseId?.let { ghId ->
                val team = profileRepo.getGreenhouseProfiles(ghId).getOrDefault(emptyList())
                SessionManager.greenhouseProfiles = team.associateBy { it.id }
            }
        }
    }
}
