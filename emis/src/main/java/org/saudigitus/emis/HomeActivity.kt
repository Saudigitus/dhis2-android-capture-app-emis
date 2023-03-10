package org.saudigitus.emis

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.dhis2.commons.Constants.PROGRAM_UID
import org.dhis2.commons.prefs.PreferenceProvider
import org.dhis2.commons.prefs.SECURE_CREDENTIALS
import org.dhis2.commons.prefs.SECURE_SERVER_URL
import org.dhis2.commons.prefs.SECURE_USER_NAME
import org.saudigitus.emis.data.model.AppConfig
import org.saudigitus.emis.data.remote.DataStoreConfig
import org.saudigitus.emis.service.APIClient
import org.saudigitus.emis.service.Basic64AuthInterceptor
import org.saudigitus.emis.ui.attendance.AttendanceScreen
import org.saudigitus.emis.ui.attendance.AttendanceViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    lateinit var dataStoreConfig: DataStoreConfig
    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MdcTheme(
                readColors = true,
                readTypography = true,
                setDefaultFontFamily = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    downloadAndStoreConfig(viewModel.config())
                    AttendanceScreen(this, viewModel)
                }
            }
        }
    }

    private fun downloadAndStoreConfig(config: AppConfig?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (config == null) {
                viewModel.authenticate()

                dataStoreConfig = APIClient.getClient("${viewModel.serverUrl()}/")
                    ?.create(DataStoreConfig::class.java)!!

                dataStoreConfig.getConfig("${viewModel.program()}")
                    .enqueue(object : Callback<AppConfig> {
                        override fun onResponse(
                            call: Call<AppConfig>,
                            response: Response<AppConfig>
                        ) {
                            viewModel.saveConfig(response.body())
                        }

                        override fun onFailure(call: Call<AppConfig>, t: Throwable) {
                            call.cancel()
                            Timber.tag("CONF").e(t)
                        }
                    })
            }
        }
    }
}