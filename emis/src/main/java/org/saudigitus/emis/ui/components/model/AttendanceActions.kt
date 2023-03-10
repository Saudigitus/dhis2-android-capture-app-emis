package org.saudigitus.emis.ui.components.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.hisp.dhis.android.core.dataelement.DataElement

data class AttendanceActions(
    val code: String? = null,
    val name: String? = null,
    val dataElement: String? = null,
    val icon: Int = 0,
    val hexColor: Long? = 0xFF00FF00,
    val actionOrder: Int? = null
) {

}