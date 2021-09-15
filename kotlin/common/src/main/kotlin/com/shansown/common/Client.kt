package com.shansown.common

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


/**
 * @author yehor.lashkul
 */
// Retrofit
val retrofitClientApi: ClientApi = RetrofitClient.apiClient

private object RetrofitClient {
    val apiClient by lazy {
        Retrofit.Builder()
            .baseUrl("http://localhost:3000/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ClientApi::class.java)
    }
}

interface ClientApi {
    @POST("echo")
    @Headers("Content-Type: application/json")
    suspend fun payloadAsync(@Body payload: String): String

    @POST("echo")
    @Headers("Content-Type: application/json")
    fun payloadSync(@Body payload: String): Call<String>
}

// Ktor
object KtorClientApi {

    private val ktorClient = HttpClient(Apache)

    suspend fun payload(payload: String): String {
        return ktorClient.post("http://localhost:3000/echo") {
            headers {
                append("Content-Type", "application/json")
            }
            body = payload
        }
    }
}
