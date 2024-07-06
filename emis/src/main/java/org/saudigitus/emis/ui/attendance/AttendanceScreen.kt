package org.saudigitus.emis.ui.attendance

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dhis2.commons.data.SearchTeiModel
import org.saudigitus.emis.R
import org.saudigitus.emis.ui.attendance.components.AttendanceSummaryDialog
import org.saudigitus.emis.ui.attendance.components.ReasonForAbsenceDialog
import org.saudigitus.emis.ui.attendance.components.Toolbar
import org.saudigitus.emis.ui.components.AttendanceButtons
import org.saudigitus.emis.ui.components.AttendanceItemState
import org.saudigitus.emis.ui.components.ItemTracker
import org.saudigitus.emis.ui.components.TextAttributeView
import org.saudigitus.emis.utils.Constants.ABSENT
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    activity: Activity,
    viewModel: AttendanceViewModel = viewModel()
) {
    val searchTeiModels by viewModel.searchTeiModel.collectAsState()
    val attendanceActions by viewModel.attendanceActions.collectAsState()
    val attendanceState by viewModel.attendanceList.collectAsState()
    val attendanceStep = viewModel.attendanceStep.collectAsState().value

    var isAbsent by remember { mutableStateOf(false) }
    var selectedAttendance by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(-1) }
    var selectedOu by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf("") }
    var selectedTei by remember { mutableStateOf("") }

    val lazyListState = rememberLazyListState()
    val fabVisibility by remember {
        mutableStateOf(lazyListState.firstVisibleItemIndex == 0)
    }

    if (isAbsent) {
        ReasonForAbsenceDialog(
            viewModel = viewModel,
            title = stringResource(R.string.reason_absence),
            themeColor = Color(colorResource(id = R.color.colorPrimary).toArgb()),
            onItemClick = { selectedReason = it },
            onCancel = { isAbsent = !isAbsent }
        ) {
            viewModel.setAttendance(
                index = selectedIndex,
                ou = selectedOu,
                tei = selectedTei,
                value = selectedAttendance,
                reasonOfAbsence = selectedReason,
            )

            isAbsent = !isAbsent
        }
    }

    if (attendanceStep == ButtonStep.SAVING) {
        var presentValue = ""
        var lateValue = ""
        var absentValue = ""

        viewModel.getSummary { present, late, absent ->
            presentValue = present
            lateValue = late
            absentValue = absent
        }

        AttendanceSummaryDialog(
            title = stringResource(R.string.attendance_summary),
            presentValue = presentValue,
            lateValue = lateValue,
            absentValue = absentValue,
            themeColor = MaterialTheme.colors.primary,
            onCancel = { viewModel.setAttendanceStep(ButtonStep.HOLD_SAVING) }
        ) {
            viewModel.clearCache()
            activity.finish()
        }
    }

    Scaffold(
        topBar = {
            Toolbar(
                title = "",
                onDatePick = { date ->
                    viewModel.attendanceEvents(date)
                }
            ) { activity.finish() }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisibility,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        if (attendanceStep == ButtonStep.HOLD_SAVING) {
                            viewModel.setAttendanceStep(ButtonStep.SAVING)
                        } else {
                            viewModel.setAttendanceStep(ButtonStep.HOLD_SAVING)
                        }
                    },
                    containerColor = MaterialTheme.colors.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (attendanceStep == ButtonStep.EDITING) {
                            Icons.Outlined.Edit
                        } else {
                            Icons.Outlined.Save
                        },
                        contentDescription = null
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = Color.White)
        ) {
            searchTeiModels?.forEach { teis ->
                item {
                    ItemTracker(
                        icoLetter = "${
                            teis.attributeValues.values.toList().getOrNull(0)
                                ?.value()?.firstOrNull() ?: ""
                        }",
                        themeColor = MaterialTheme.colors.primary,
                        onClick = {}
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AttributeMapper(teiModel = teis, viewModel = viewModel)

                            if (attendanceStep == ButtonStep.EDITING) {
                                AttendanceItemState(teis.tei.uid(), attendanceState)
                            } else {
                                attendanceActions?.let {
                                    AttendanceButtons(
                                        viewModel = viewModel,
                                        tei = teis.tei.uid(),
                                        actions = it
                                    ) { index, tei, attendance ->
                                        isAbsent = attendance == ABSENT
                                        if (attendance.lowercase() != ABSENT.lowercase()) {
                                            viewModel.setAttendance(
                                                index = index,
                                                ou = teis.tei.organisationUnit().toString(),
                                                tei = tei.toString(),
                                                value = attendance
                                            )
                                        } else {
                                            selectedAttendance = attendance
                                            selectedOu = teis.tei.organisationUnit().toString()
                                            selectedIndex = index
                                            selectedTei = tei.toString()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttributeMapper(
    teiModel: SearchTeiModel,
    viewModel: AttendanceViewModel
) {
    val teiAttributes = viewModel.teiAttributes(teiModel)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        teiAttributes.let {
            TextAttributeView(
                attribute = "${it.val0()?.first}",
                attributeValue = "${it.val0()?.second}"
            )
            TextAttributeView(
                attribute = "${it.val1()?.first}",
                attributeValue = "${it.val1()?.second}"
            )
            TextAttributeView(
                attribute = "${it.val2()?.first}",
                attributeValue = "${it.val2()?.second}"
            )
        }
    }
}