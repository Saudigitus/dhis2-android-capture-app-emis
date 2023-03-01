package org.saudigitus.emis.data.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dhis2.Bindings.userFriendlyValue
import org.dhis2.commons.data.SearchTeiModel
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.saudigitus.emis.data.local.DataManager
import org.saudigitus.emis.utils.NetworkUtils
import javax.inject.Inject

class DataManagerImpl
@Inject constructor(
    private val context: Context,
    private val d2: D2
): DataManager{

    override suspend fun trackedEntityInstances(
        program: String
    ) =
        withContext(Dispatchers.IO) {
            val repository = d2.trackedEntityModule().trackedEntityInstanceQuery()

            return@withContext if (NetworkUtils.isOnline(context)) {
                repository.onlineFirst().allowOnlineCache().eq(true)
                    .byOrgUnits().eq("aDM8y4dwpki")
                    .byProgram().eq(program)
                    .blockingGet()
                    .flatMap { tei ->
                        listOf(tei)
                    }.map { tei ->
                        transform(tei, program)
                    }
            } else {
                repository
                    .offlineOnly().allowOnlineCache().eq(false)
                    .byOrgUnits().eq("aDM8y4dwpki")
                    .byProgram().eq(program)
                    .blockingGet()
                    .flatMap { tei ->
                        listOf(tei)
                    }.map { tei ->
                        transform(tei, program)
                    }
            }
        }


    private fun transform(
        tei: TrackedEntityInstance,
        program: String?
    ): SearchTeiModel {
        val searchTei = SearchTeiModel()
        searchTei.tei = tei

        if (tei.trackedEntityAttributeValues() != null) {
            if (program != null) {
                val programAttributes = d2.programModule().programTrackedEntityAttributes()
                    .byProgram().eq(program)
                    .byDisplayInList().isTrue
                    .orderBySortOrder(RepositoryScope.OrderByDirection.ASC)
                    .blockingGet()

                for (programAttribute in programAttributes) {
                    val attribute = d2.trackedEntityModule().trackedEntityAttributes()
                        .uid(programAttribute.trackedEntityAttribute()!!.uid())
                        .blockingGet()

                    for (attrValue in tei.trackedEntityAttributeValues()!!) {
                        if (attrValue.trackedEntityAttribute() == attribute.uid()) {
                            addAttribute(searchTei, attrValue, attribute)
                            break
                        }
                    }
                }
            } else {
                val typeAttributes = d2.trackedEntityModule().trackedEntityTypeAttributes()
                    .byTrackedEntityTypeUid().eq(searchTei.tei.trackedEntityType())
                    .byDisplayInList().isTrue
                    .blockingGet()
                for (typeAttribute in typeAttributes) {
                    val attribute = d2.trackedEntityModule().trackedEntityAttributes()
                        .uid(typeAttribute.trackedEntityAttribute()!!.uid())
                        .blockingGet()
                    for (attrValue in tei.trackedEntityAttributeValues()!!) {
                        if (attrValue.trackedEntityAttribute() == attribute.uid()) {
                            addAttribute(searchTei, attrValue, attribute)
                            break
                        }
                    }
                }
            }
        }
        return searchTei
    }

    private fun addAttribute(
        searchTei: SearchTeiModel,
        attrValue: TrackedEntityAttributeValue,
        attribute: TrackedEntityAttribute
    ) {
        val friendlyValue = attrValue.userFriendlyValue(d2)

        val attrValueBuilder = TrackedEntityAttributeValue.builder()
        attrValueBuilder.value(friendlyValue)
            .created(attrValue.created())
            .lastUpdated(attrValue.lastUpdated())
            .trackedEntityAttribute(attrValue.trackedEntityAttribute())
            .trackedEntityInstance(searchTei.tei.uid())
        searchTei.addAttributeValue(attribute.displayFormName(), attrValueBuilder.build())
    }
}