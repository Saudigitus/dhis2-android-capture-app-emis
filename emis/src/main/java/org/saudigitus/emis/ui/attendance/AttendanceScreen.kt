package org.saudigitus.emis.ui.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import org.saudigitus.emis.ui.attendance.components.Toolbar
import org.saudigitus.emis.ui.components.AttendanceButtons
import org.saudigitus.emis.ui.components.ItemTracker
import org.saudigitus.emis.ui.components.PreviewAttendanceActions
import org.saudigitus.emis.ui.components.TextAttributeView
import org.saudigitus.emis.ui.components.model.AttendanceActions
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel = viewModel()
) {

    val searchTeiModels by viewModel.searchTeiModel.collectAsState()

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
            searchTeiModels?.forEach { teis ->
                item {
                    ItemTracker(
                        icoLetter = "${teis.attributeValues.values.toList()[0].value()?.first()}",
                        themeColor = Color(0xFF0984D9),
                        onClick = {}
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                TextAttributeView(
                                    attribute = teis.attributeValues.keys.toList()[0],
                                    attributeValue = "${teis.attributeValues.values.toList()[0].value()}"
                                )

                                TextAttributeView(
                                    attribute = teis.attributeValues.keys.toList()[1],
                                    attributeValue = "${teis.attributeValues.values.toList()[1].value()}"
                                )

                                TextAttributeView(
                                    attribute = teis.attributeValues.keys.toList()[2],
                                    attributeValue = "${teis.attributeValues.values.toList()[2].value()}"
                                )
                            }
                            AttendanceActions {
                                Timber.tag("ATTENDANCE").e(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceActions(onClick: (attendanceState: String) -> Unit) {
    val actions = mutableListOf(
        AttendanceActions("Present", iconVector = Icons.Outlined.Check, tint = Color.Green),
        AttendanceActions("Late", iconVector = Icons.Outlined.Schedule, tint = Color(0xFFF79706)),
        AttendanceActions("Absent", iconVector = Icons.Outlined.Close, tint = Color.Red),
    )

    AttendanceButtons(actions = actions) {
        onClick(it)
    }
}