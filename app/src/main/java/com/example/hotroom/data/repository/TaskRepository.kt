package com.example.hotroom.data.repository

import com.example.hotroom.data.SupabaseClient
import com.example.hotroom.data.model.CareTask
import io.github.jan.supabase.postgrest.postgrest

class TaskRepository {

    private val postgrest = SupabaseClient.client.postgrest

    suspend fun getTodayTasks(userId: String, date: String): Result<List<CareTask>> {
        return try {
            val tasks = postgrest.from("care_tasks")
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("scheduled_date", date)
                    }
                }
                .decodeList<CareTask>()
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasksByMonth(userId: String, year: Int, month: Int): Result<List<CareTask>> {
        return try {
            val startDate = "$year-${month.toString().padStart(2, '0')}-01"
            val endDate = if (month == 12) "${year + 1}-01-01"
            else "$year-${(month + 1).toString().padStart(2, '0')}-01"

            val tasks = postgrest.from("care_tasks")
                .select() {
                    filter {
                        eq("user_id", userId)
                        gte("scheduled_date", startDate)
                        lt("scheduled_date", endDate)
                    }
                }
                .decodeList<CareTask>()
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasksForPlant(plantId: String): Result<List<CareTask>> {
        return try {
            val tasks = postgrest.from("care_tasks")
                .select() {
                    filter { eq("plant_id", plantId) }
                }
                .decodeList<CareTask>()
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addTask(task: CareTask): Result<Unit> {
        return try {
            postgrest.from("care_tasks").insert(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markComplete(taskId: String, completedBy: String): Result<Unit> {
        return try {
            postgrest.from("care_tasks").update(
                mapOf(
                    "is_completed" to true,
                    "completed_at" to java.time.Instant.now().toString(),
                    "completed_by" to completedBy
                )
            ) {
                filter { eq("id", taskId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * O'simlik sug'orilganda uning bugungi sug'orish vazifalarini bajarildi qilish
     */
    suspend fun completeWateringTasksForPlant(plantId: String, date: String, completedBy: String): Result<Unit> {
        return try {
            postgrest.from("care_tasks").update(
                mapOf(
                    "is_completed" to true,
                    "completed_at" to java.time.Instant.now().toString(),
                    "completed_by" to completedBy
                )
            ) {
                filter {
                    eq("plant_id", plantId)
                    eq("task_type", "watering")
                    eq("scheduled_date", date)
                    eq("is_completed", false)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            postgrest.from("care_tasks").delete {
                filter { eq("id", taskId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
