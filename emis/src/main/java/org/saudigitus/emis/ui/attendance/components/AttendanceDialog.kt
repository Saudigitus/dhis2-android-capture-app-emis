package org.saudigitus.emis.ui.attendance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.saudigitus.emis.R
import org.saudigitus.emis.ui.attendance.AttendanceViewModel
import org.saudigitus.emis.ui.components.TextButton
import org.saudigitus.emis.ui.theme.Green
import org.saudigitus.emis.ui.theme.Orange
import org.saudigitus.emis.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReasonForAbsenceDialog(
    viewModel: AttendanceViewModel,
    title: String,
    themeColor: Color,
    onItemClick: (String) -> Unit
) {
    var selectedText by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val interactionSource = remember { MutableInteractionSource() }
    val paddingValue = if (selectedIndex >= 0) {
        8.dp
    } else {
        0.dp
    }

    val icon = if (isExpanded) {
        Icons.Default.ArrowDropUp
    } else {
        Icons.Default.ArrowDropDown
    }

    DialogTemplate(
        title = title,
        themeColor = themeColor
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    }
                    .shadow(
                        elevation = 8.dp,
                        ambientColor = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(30.dp),
                        clip = false
                    )
                    .offset(0.dp, 0.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(30.dp)),
                readOnly = true,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.reason))
                },
                trailingIcon = {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                shape = RoundedCornerShape(30.dp),
                interactionSource = interactionSource,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color.Black),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White
                )
            )

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    .background(shape = RoundedCornerShape(16.dp), color = Color.White),
                offset = DpOffset(x = 0.dp, y = 2.dp)
            ) {
                viewModel.reasonOfAbsence.collectAsState().value?.forEachIndexed { index, reason ->
                    DropdownMenuItem(
                        onClick = {
                            selectedText = reason.displayName().toString()
                            selectedIndex = index
                            onItemClick.invoke(reason.code().toString())
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = if (selectedIndex == index) {
                                        colorResource(R.color.bg_gray_f1f)
                                    } else {
                                        Color.White
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(paddingValue),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = reason.displayName().toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceSummaryDialog(
    title: String,
    presentValue: String,
    lateValue: String,
    absentValue: String,
    themeColor: Color,
    onCancel: () -> Unit,
    onDone: () -> Unit
) {
    DialogTemplate(
        title = title,
        themeColor = themeColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryComponent(
                summary = presentValue,
                containerColor = Green,
                icon = Icons.Outlined.Check
            )

            SummaryComponent(
                summary = lateValue,
                containerColor = Orange,
                icon = Icons.Outlined.Schedule
            )

            SummaryComponent(
                summary = absentValue,
                containerColor = Red,
                icon = Icons.Outlined.Close
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                title = stringResource(R.string.cancel),
                containerColor = Color.White,
                contentColor = themeColor
            ) { onCancel.invoke() }

            TextButton(
                title = stringResource(R.string.done),
                containerColor = Color.White,
                contentColor = themeColor
            ) { onDone.invoke() }
        }
    }
}

@Composable
private fun SummaryComponent(
    title: String? = null,
    summary: String,
    containerColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .width(95.dp)
            .padding(10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White
            )
            Text(text = summary)
        }
    }
}

@Composable
private fun DialogTemplate(
    title: String,
    themeColor: Color,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(32.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(color = themeColor),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 16.dp),
                    color = Color.White
                )
            }
            content()
        }
    }
}
