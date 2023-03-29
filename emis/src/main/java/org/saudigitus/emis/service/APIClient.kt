package org.saudigitus.emis.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object APIClient {
    fun getClient(baseUrl: String): Retrofit? {
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(Basic64AuthInterceptor)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .client(client)
            .build()
    }
}
