package org.saudigitus.emis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import org.saudigitus.emis.data.remote.DataStoreConfig
import org.saudigitus.emis.service.APIClient
import org.saudigitus.emis.ui.attendance.AttendanceScreen
import org.saudigitus.emis.ui.attendance.AttendanceViewModel
import org.saudigitus.emis.utils.Commons.setProgramTheme
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private lateinit var dataStoreConfig: DataStoreConfig
    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreConfig = APIClient.getClient("${viewModel.server()}/")
            ?.create(DataStoreConfig::class.java)!!

        viewModel.config(dataStoreConfig)
        setProgramTheme(this, viewModel.theme.value)

        setContent {
            MdcTheme(
                readColors = true,
                readTypography = true,
                setDefaultFontFamily = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AttendanceScreen(this)
                }
            }
        }
    }
}
