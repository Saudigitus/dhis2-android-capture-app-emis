package org.saudigitus.emis.ui.components.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AttendanceActions(
    val name: String,
    val icon: Int = 0,
    val iconVector: ImageVector? = null,
    val tint: Color
)
