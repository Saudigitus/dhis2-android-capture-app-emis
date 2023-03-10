package org.saudigitus.emis.data

import android.content.Context
import org.hisp.dhis.android.BuildConfig
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2Configuration
import org.hisp.dhis.android.core.D2Manager
import org.saudigitus.emis.R

object Sdk {
    fun d2(context: Context): D2 {
        return try {
            D2Manager.getD2()
        } catch (e: Exception) {
            D2Manager.blockingInstantiateD2(
                D2Configuration.builder()
                    .appName(context.getString(R.string.app_name))
                    .appVersion(BuildConfig.VERSION_NAME)
                    .readTimeoutInSeconds(10 * 60)
                    .connectTimeoutInSeconds(10 * 60)
                    .writeTimeoutInSeconds(10 * 60)
                    .context(context)
                    .build()
            )!!
        }
    }
}
