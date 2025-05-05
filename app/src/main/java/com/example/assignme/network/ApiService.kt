package com.example.assignme.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/webhooks/rest/webhook")
    fun sendMessage(@Body message: Message): Call<List<ResponseMessage>>
}
