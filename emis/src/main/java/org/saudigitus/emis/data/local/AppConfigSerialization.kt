package org.saudigitus.emis.data.local

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.saudigitus.emis.data.model.AppConfig
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

object AppConfigSerialization: Serializer<AppConfig> {
    override val defaultValue: AppConfig
        get() = AppConfig()

    override suspend fun readFrom(input: InputStream): AppConfig {
        return try {
            Json.decodeFromString(
                deserializer = AppConfig.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: java.lang.Exception) {
            Timber.e(e)

            defaultValue
        }
    }

    override suspend fun writeTo(t: AppConfig, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = AppConfig.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}