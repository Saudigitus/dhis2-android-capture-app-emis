package org.saudigitus.emis.data.local

import org.dhis2.commons.data.SearchTeiModel
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

interface DataManager {

    suspend fun trackedEntityInstances(ou: String, program: String): List<SearchTeiModel>

}