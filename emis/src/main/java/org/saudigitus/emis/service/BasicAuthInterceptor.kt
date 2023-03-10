package org.saudigitus.emis.service

import android.util.Base64
import java.nio.charset.StandardCharsets
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

object Basic64AuthInterceptor : Interceptor {

    private lateinit var credentials: String

    fun setCredential(username: String, password: String) {
        credentials = "$username:$password"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val base64: String = Base64.encodeToString(
            credentials.toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )

        val authRequest: Request = request.newBuilder()
            .header("Accept", "application/json")
            .header("Authorization", "Basic $base64")
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Headers", "X-Requested-With")
            .header("Access-Control-Max-Age", "60")
            .build()

        return chain.proceed(authRequest)
    }
}
