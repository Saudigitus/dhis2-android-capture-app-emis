package org.saudigitus.emis.ui.attendance

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.saudigitus.emis.ui.attendance.components.Toolbar
import org.saudigitus.emis.ui.components.PreviewItemTracker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen() {

    Scaffold(
        topBar = {
            Toolbar(title = "", subtitle = "16/02/2023")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            for (i in 0..10) {
                item {
                    PreviewItemTracker(i)
                }
            }
        }
    }
}