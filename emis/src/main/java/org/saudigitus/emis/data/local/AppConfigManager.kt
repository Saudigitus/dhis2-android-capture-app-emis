package org.saudigitus.emis.data.local

import kotlinx.coroutines.flow.Flow
import org.saudigitus.emis.data.model.AppConfig

interface AppConfigManager {
    suspend fun save(appConfig: AppConfig)
    fun getAppConfigByProgram(program: String): Flow<AppConfig>
}
