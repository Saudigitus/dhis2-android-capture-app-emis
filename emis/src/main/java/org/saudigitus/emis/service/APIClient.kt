package org.saudigitus.emis.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object APIClient {
    fun getClient(baseUrl: String): Retrofit? {
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(Basic64AuthInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .client(client)
            .build()
    }
}
