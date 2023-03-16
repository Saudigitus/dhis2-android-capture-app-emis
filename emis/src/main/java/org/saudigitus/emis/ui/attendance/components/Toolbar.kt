package org.saudigitus.emis.ui.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import org.dhis2.commons.date.DateUtils
import org.saudigitus.emis.R
import org.saudigitus.emis.ui.components.TextButton
import org.saudigitus.emis.ui.theme.Rubik
import org.saudigitus.emis.utils.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    title: String,
    subtitle: String? = null,
    containerColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = Color.White,
    onDatePick: (String?) -> Unit,
    onNavigationBack: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        initialDisplayMode = DisplayMode.Picker
    )
    var isCalendar by remember { mutableStateOf(false) }

    var selectedDate by remember {
        mutableStateOf(
            DateUtil.formatDate(
                datePickerState.selectedDateMillis ?: DateUtils.getInstance().today.time
            )
        )
    }

    if (isCalendar) {
        DatePickerDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    title = stringResource(R.string.done),
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    selectedDate = DateUtil.formatDate(datePickerState.selectedDateMillis ?: 0)
                    onDatePick.invoke(selectedDate)
                    isCalendar = !isCalendar
                }
            },
            dismissButton = {
                TextButton(
                    title = stringResource(R.string.cancel),
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colors.error
                ) { isCalendar = !isCalendar }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = true
            ),
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                todayContentColor = MaterialTheme.colors.primary,
                todayDateBorderColor = MaterialTheme.colors.primary,
                selectedDayContainerColor = MaterialTheme.colors.primary,
                selectedYearContainerColor = MaterialTheme.colors.primary
            )
        ) {
            DatePicker(
                state = datePickerState,
                title = { Text(text = stringResource(R.string.attendance_date), fontFamily = Rubik) },
                showModeToggle = false
            )
        }
    }

    TopAppBar(
        title = {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title.ifEmpty { stringResource(R.string.attendance) },
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = Rubik
                )

                if (subtitle != null || selectedDate != null) {
                    Text(
                        text = selectedDate!!,
                        style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        fontSize = 14.sp,
                        lineHeight = 24.sp,
                        fontFamily = Rubik
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationBack.invoke() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(onClick = { isCalendar = !isCalendar }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.calendar)
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        )
    )
}
