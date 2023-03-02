package org.saudigitus.emis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import org.saudigitus.emis.data.model.AppConfig
import org.saudigitus.emis.data.remote.DataStoreConfig
import org.saudigitus.emis.service.APIClient
import org.saudigitus.emis.service.Basic64AuthInterceptor
import org.saudigitus.emis.ui.attendance.AttendanceScreen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    lateinit var dataStoreConfig: DataStoreConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //EmisCaptureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Basic64AuthInterceptor.setCredential("", "")
                    dataStoreConfig = APIClient
                        .getClient("https://dhis2dev.waliku.org/dev/api/")
                        ?.create(DataStoreConfig::class.java)!!

                    dataStoreConfig.getConfig().enqueue(object : Callback<AppConfig> {
                        override fun onResponse(
                            call: Call<AppConfig>,
                            response: Response<AppConfig>
                        ) {
                            Timber.tag("CONF").e("${response.body()?.filters}")
                        }

                        override fun onFailure(call: Call<AppConfig>, t: Throwable) {
                            Timber.tag("CONF").e(t)
                        }
                    })

                    AttendanceScreen()
                }
            //}
        }
    }


}