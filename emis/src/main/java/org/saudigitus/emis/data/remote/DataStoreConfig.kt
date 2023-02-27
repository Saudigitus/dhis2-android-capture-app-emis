package org.saudigitus.emis.data.remote

import org.saudigitus.emis.data.model.AppConfig
import retrofit2.Call
import retrofit2.http.GET

interface DataStoreConfig {

    @GET("dataStore/emisApps/AkDQ4JvN3dW")
    fun getConfig(): Call<AppConfig>
}