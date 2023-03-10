package org.saudigitus.emis.ui.attendance

data class AttendanceUiState(
    val btnIndex: Int = -1,
    val btnId: String? = null,
    val iconTint: Long? = null,
    val buttonState:  AttendanceButtonState? = null
)

data class AttendanceButtonState(
    val buttonType: ButtonType? = null,
    val containerColor: Long? = null,
    val contentColor: Long? = null
)

enum class ButtonType {
    PRESENT,
    LATE,
    ABSENT
}

enum class ButtonStep {
    EDITING,
    HOLD_SAVING,
    SAVING
}
