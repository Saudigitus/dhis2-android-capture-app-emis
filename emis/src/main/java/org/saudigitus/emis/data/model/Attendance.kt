package org.saudigitus.emis.data.model

data class Attendance(
    val tei: String,
    val dataElement: String,
    val value: String,
    val reasonDataElement: String? = null,
    val reasonOfAbsence: String? = null,
    val date: String
)

data class AttendanceLineList(
    val program: String? = null,
    val programStage: String? = null,
    val dataElement: String? = null,
    val reasonDataElement: String? = null
)