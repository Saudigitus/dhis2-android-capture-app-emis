package org.saudigitus.emis.data.remote

import org.saudigitus.emis.data.model.AppConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DataStoreConfig {

    @GET("dataStore/emisApps/{program}")
    fun getConfig(@Path("program") program: String): Call<AppConfig>
}
