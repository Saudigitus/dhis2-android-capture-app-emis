package org.saudigitus.emis.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.layoutId
import org.saudigitus.emis.data.model.Attendance
import org.saudigitus.emis.ui.attendance.AttendanceUiState
import org.saudigitus.emis.ui.attendance.AttendanceViewModel
import org.saudigitus.emis.ui.components.model.AttendanceActions
import org.saudigitus.emis.ui.theme.White
import org.saudigitus.emis.utils.Constants.ABSENT
import org.saudigitus.emis.utils.Constants.LATE
import org.saudigitus.emis.utils.Constants.PRESENT
import timber.log.Timber

@Composable
private fun ItemContainer(
    content: @Composable() (RowScope.() -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun CardItemContainer(
    onClick: () -> Unit,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier
            .clickable { onClick.invoke() },
        shape = RoundedCornerShape(0.dp),
        content = content,
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(0.dp, Color.White)
    )
}

@Composable
fun AttendanceButtons(
    viewModel: AttendanceViewModel,
    tei: String,
    actions: List<AttendanceActions>,
    onClick: (
        index: Int,
        tei: String?,
        attendanceState: String
    ) -> Unit
) {
    val btnState by viewModel.attendanceBtnState.collectAsState()
    var btnCode by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(-1) }

    Row(
        modifier = Modifier.layoutId(layoutId = tei),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        actions.forEachIndexed { index, action ->
            IconButton(
                onClick = {
                    btnCode = action.code.toString()
                    selectedIndex = index

                    onClick.invoke(
                        index,
                        tei,
                        action.code.toString()
                    )
                },
                modifier = Modifier
                    .border(
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = MaterialTheme.shapes.small.copy(CornerSize(32.dp))
                    )
                    .size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(
                        getContainerColor(btnState, tei, action.code.toString(), selectedIndex)
                    ),
                    contentColor = Color(
                        getContentColor(btnState, tei, action.code.toString(), selectedIndex)
                            ?: action.hexColor ?: White.value.toLong()
                    )
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(action.icon),
                    contentDescription = action.name
                )
            }
        }
    }
}

private fun getContainerColor(
    btnState: MutableList<AttendanceUiState>,
    tei: String,
    code: String,
    selectedIndex: Int,
    actions: List<AttendanceActions>? = null
): Long {
    val attendance = btnState.find { it.btnId == tei }
    val actionIndex = actions?.indexOfFirst { it.code == attendance?.btnId }

    return if (attendance != null &&
        attendance.buttonState?.buttonType?.name?.lowercase() == code &&
        (attendance.btnIndex == selectedIndex)
    ) {
        attendance.buttonState.containerColor ?: 0L
    } else if (attendance != null && selectedIndex == -1 && attendance.btnIndex == actionIndex) {
        Timber.tag("QAD").e("INNN")
        attendance.buttonState?.containerColor ?: 0L
    } else {
        White.value.toLong()
    }
}

private fun getContentColor(
    btnState: MutableList<AttendanceUiState>,
    tei: String,
    code: String,
    selectedIndex: Int,
    actions: List<AttendanceActions>? = null
): Long? {
    val attendance = btnState.find { it.btnId == tei }
    val actionIndex = actions?.indexOfFirst { it.code == attendance?.btnId }

    return if (attendance != null &&
        attendance.buttonState?.buttonType?.name?.lowercase() == code &&
        (attendance.btnIndex == selectedIndex || attendance.btnIndex == actionIndex)
    ) {
        attendance.buttonState.contentColor
    } else {
        attendance?.buttonState?.contentColor
    }
}

@Composable
fun AttendanceItemState(
    tei: String,
    attendanceState: List<Attendance?>?
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (attendanceState == null || attendanceState.isEmpty()) {
            Icon(
                imageVector = Icons.Filled.Help,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.LightGray
            )
        }

        attendanceState?.let { attendances ->
            for (attendance in attendances) {
                if (attendance?.tei == tei) {
                    when (attendance.value) {
                        PRESENT -> {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = attendance.value,
                                modifier = Modifier.size(32.dp),
                                tint = Color.Green
                            )
                            break
                        }
                        LATE -> {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = attendance.value,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFFF79706)
                            )
                            break
                        }
                        ABSENT -> {
                            Icon(
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = attendance.value,
                                modifier = Modifier.size(32.dp),
                                tint = Color.Red
                            )
                            break
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.Help,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.LightGray
                    )
                    break
                }
            }
        }
    }
}

@Composable
fun ItemTracker(
    painter: Painter? = null,
    icoLetter: String? = null,
    themeColor: Color,
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit)
) {
    CardItemContainer(onClick) {
        ItemContainer {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(color = themeColor)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                MetadataIcon(
                    cornerShape = 100.dp,
                    backgroundColor = themeColor,
                    size = 48.dp,
                    paddingAll = 5.dp,
                    painter = painter,
                    colorFilter = ColorFilter.tint(Color.White)
                )
                if (painter == null) {
                    Text(text = "$icoLetter", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            content()
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(.83f)
                .align(Alignment.End)
                .wrapContentWidth(Alignment.End, false)
        )
    }
}
