package com.example.gamevault.core.network

import com.example.gamevault.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit único para consumir la API de RAWG.
 * La API key se inyecta como query param desde el repositorio y se lee
 * desde BuildConfig (definida en local.properties).
 */
object ApiClient {
    private const val BASE_URL = "https://api.rawg.io/api/"

    // API key de RAWG leída de local.properties vía BuildConfig.
    val API_KEY: String = BuildConfig.RAWG_API_KEY

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val rawgApi: RawgApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RawgApi::class.java)
    }
}
