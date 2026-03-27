package com.example.hotroom.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    // TODO: Replace with your Supabase project URL and anon key
    private const val SUPABASE_URL = "https://skqdhnpprcttvcbemjhw.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNrcWRobnBwcmN0dHZjYmVtamh3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ2Mzg4MjYsImV4cCI6MjA5MDIxNDgyNn0.raU2IQSl4g5KJ37AvgHX9UJdmJakLcAATqiFJuRAHys"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}
