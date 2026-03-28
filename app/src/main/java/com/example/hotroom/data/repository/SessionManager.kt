package com.example.hotroom.data.repository

import com.example.hotroom.data.model.Profile

/**
 * Tizimga kirgan foydalanuvchining ma'lumotlarini dastur butun ishlashi bo'yi xotirada eslab qolish 
 * uchun global Obyekt. Buning yordamida bitta guruhning ichidagi "greenhouse_id" nazorat qilinadi.
 */
object SessionManager {
    var currentProfile: Profile? = null
    var greenhouseProfiles: Map<String, Profile> = emptyMap()
    
    val greenhouseId: String?
        get() = currentProfile?.greenhouseId
        
    val role: String
        get() = currentProfile?.role ?: "gardener"
}
