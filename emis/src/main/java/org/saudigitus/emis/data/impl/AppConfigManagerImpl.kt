package org.saudigitus.emis.data.impl

import android.content.Context
import androidx.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.saudigitus.emis.data.local.AppConfigManager
import org.saudigitus.emis.data.local.AppConfigSerialization
import org.saudigitus.emis.data.model.AppConfig

val Context.dataStore by dataStore("emis-app-config.json", AppConfigSerialization)

class AppConfigManagerImpl
@Inject constructor(
    @ApplicationContext val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppConfigManager {
    override suspend fun save(appConfig: AppConfig) {
        withContext(ioDispatcher) {
            context.dataStore.updateData {
                it.copy(
                    filters = appConfig.filters?.toPersistentList(),
                    linelist = appConfig.linelist?.toPersistentList(),
                    programs = appConfig.programs
                )
            }
        }
    }

    override fun getAppConfigByProgram(program: String): Flow<AppConfig> {
        return context.dataStore.data.filter {
            it.programs == program
        }
    }

    override suspend fun isConfigNull(program: String) =
        withContext(ioDispatcher) {
            val result = getAppConfigByProgram(program).firstOrNull()

            return@withContext result != null
        }
}
