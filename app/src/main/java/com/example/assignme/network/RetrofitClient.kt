package com.example.assignme.network

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//cd C:\Users\ryanc\Documents\RasaBot
// rasa shell
//admin
object RetrofitClient {
    @Volatile private var retrofit: Retrofit? = null
    var BASE_URL = "http://192.168.1.103:5050" // ⚠️ Update IP with `ipconfig` if network changes

    fun init(context: Context) {
        val newBaseUrl = detectServerIp(context)
        if (newBaseUrl != BASE_URL || retrofit == null) {
            BASE_URL = newBaseUrl
            Log.d("RetrofitClient", "Updated BASE_URL: $BASE_URL")
            retrofit = createRetrofitInstance()
        }
    }
    // ✅ Fix: Ensure instance always returns `ApiService` safely
    val instance: ApiService
        get() {
            if (retrofit == null) {
                retrofit = createRetrofitInstance()
            }
            return retrofit!!.create(ApiService::class.java)
        }

    private fun createRetrofitInstance(): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Ensures stable connection
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL) // ✅ Removed unnecessary "/"
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun detectServerIp(context: Context): String {
        // return emulator
        return "http://10.0.2.2:5050"
    }
}
