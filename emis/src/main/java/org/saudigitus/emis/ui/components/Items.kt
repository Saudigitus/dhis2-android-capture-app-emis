package org.saudigitus.emis.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.saudigitus.emis.R
import org.saudigitus.emis.ui.components.model.AttendanceActions

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
        content = content
    )
}

@Composable
fun AttendanceButtons(
    actions: List<AttendanceActions>,
    onClick: (attendanceState: String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        actions.forEach {
            IconButton(
                onClick = { onClick.invoke(it.name) },
                modifier = Modifier
                    .border(
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = MaterialTheme.shapes.small.copy(CornerSize(100.dp))
                    )
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = it.iconVector ?: ImageVector.vectorResource(it.icon),
                    contentDescription = it.name,
                    tint = it.tint
                )
            }
        }
    }
}

@Composable
fun ItemTracker(
    painter: Painter? = null,
    themeColor: Color,
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit)
) {
    CardItemContainer(onClick) {
        ItemContainer {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(color = colorResource(R.color.purple_500))
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
                    Text(text = "A", color = Color.White)
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

@Preview
@Composable
fun PreviewAttendanceActions() {
    val actions = mutableListOf(
        AttendanceActions("Present", iconVector = Icons.Outlined.Check, tint = Color.Green),
        AttendanceActions("Late", iconVector = Icons.Outlined.Schedule, tint = Color(0xFFF79706)),
        AttendanceActions("Absent", iconVector = Icons.Outlined.Close, tint = Color.Red),
    )

    AttendanceButtons(actions = actions) {}
}

@Preview
@Composable
fun PreviewItemTracker(i: Int = 0) {
    ItemTracker(
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
                ) {
                    Text(text = "Full name:")
                    Text(text = "Alpha$i Beta")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
                ) {
                    Text(text = "Gender:")
                    Text(text = "Female")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
                ) {
                    Text(text = "Age:")
                    Text(text = "1$i")
                }
            }
            PreviewAttendanceActions()
        }
    }
}