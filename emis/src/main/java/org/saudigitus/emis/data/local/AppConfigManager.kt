package org.saudigitus.emis.data.source.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.saudigitus.emis.data.model.AppConfig
import org.saudigitus.emis.data.model.Menu


interface AppConfigManager {
    suspend fun save(appConfig: AppConfig)
    fun getAppConfigByProgram(program: String): Flow<AppConfig>
}