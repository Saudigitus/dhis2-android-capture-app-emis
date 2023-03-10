package org.saudigitus.emis.data.local

import org.dhis2.commons.data.SearchTeiModel
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.saudigitus.emis.data.model.Attendance

interface DataManager {

    suspend fun save(
        ou: String,
        program: String,
        programStage: String,
        attendance: Attendance
    )
    suspend fun trackedEntityInstances(ou: String, program: String): List<SearchTeiModel>
    suspend fun event(
        program: String,
        programStage: String,
        dataElement: String,
        teis: List<String>,
        date: String?
    ): List<Attendance?>
    suspend fun getOptions(dataElement: String): List<Option>?
    suspend fun getProgramStage(uid: String): ProgramStage
}