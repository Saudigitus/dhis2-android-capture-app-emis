package org.saudigitus.emis.data.impl

import android.content.Context
import java.sql.Date
import java.util.Collections
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dhis2.Bindings.userFriendlyValue
import org.dhis2.commons.data.SearchTeiModel
import org.dhis2.commons.date.DateUtils
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.saudigitus.emis.data.local.DataManager
import org.saudigitus.emis.data.model.Attendance
import org.saudigitus.emis.utils.Constants.DEFAULT
import org.saudigitus.emis.utils.DateUtil
import org.saudigitus.emis.utils.NetworkUtils
import timber.log.Timber

class DataManagerImpl
@Inject constructor(
    private val context: Context,
    private val d2: D2,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataManager {

    private fun getAttributeOptionCombo() =
        d2.categoryModule().categoryOptionCombos()
            .byDisplayName().eq(DEFAULT).one().blockingGet().uid()

    private fun createEventProjection(
        tei: String,
        ou: String,
        program: String,
        programStage: String
    ): String {
        val enrollment = d2.enrollmentModule().enrollments()
            .byTrackedEntityInstance().eq(tei)
            .one().blockingGet()

        return d2.eventModule().events()
            .blockingAdd(
                EventCreateProjection.builder()
                    .organisationUnit(ou)
                    .program(program).programStage(programStage)
                    .attributeOptionCombo(getAttributeOptionCombo())
                    .enrollment(enrollment.uid()).build()
            )
    }

    private fun eventUid(
        tei: String,
        program: String,
        programStage: String,
        date: String?
    ): String? {
        return d2.eventModule().events()
            .byTrackedEntityInstanceUids(Collections.singletonList(tei))
            .byProgramUid().eq(program)
            .byProgramStageUid().eq(programStage)
            .byEventDate().eq(Date.valueOf(date.toString()))
            .one().blockingGet()?.uid()
    }

    override suspend fun save(
        ou: String,
        program: String,
        programStage: String,
        attendance: Attendance
    ): Unit =
        withContext(ioDispatcher) {
            try {
                val uid = eventUid(
                    attendance.tei,
                    program,
                    programStage,
                    attendance.date
                ) ?: createEventProjection(
                    attendance.tei,
                    ou,
                    program,
                    programStage
                )

                d2.trackedEntityModule().trackedEntityDataValues()
                    .value(uid, attendance.dataElement)
                    .blockingSet(attendance.value)

                if (attendance.reasonDataElement != null && attendance.reasonOfAbsence != null) {
                    attendance.reasonOfAbsence.let {
                        if (it.isNotEmpty() && it.isNotBlank()) {
                            d2.trackedEntityModule().trackedEntityDataValues()
                                .value(uid, attendance.reasonDataElement).blockingSet(it)
                        }
                    }
                }

                val repository = d2.eventModule().events().uid(uid)
                repository.setEventDate(Date.valueOf(attendance.date))
            } catch (e: Exception) {
                Timber.tag("SAVE_EVENT").e(e)
            }
        }

    override suspend fun trackedEntityInstances(
        ou: String,
        program: String
    ) =
        withContext(ioDispatcher) {
            val repository = d2.trackedEntityModule().trackedEntityInstanceQuery()

            return@withContext if (NetworkUtils.isOnline(context)) {
                repository.allowOnlineCache().eq(true).offlineFirst()
                    .byOrgUnits().eq(ou)
                    .byProgram().eq(program)
                    .blockingGet()
                    .flatMap { tei ->
                        listOf(tei)
                    }.map { tei ->
                        transform(tei, program)
                    }
            } else {
                repository.allowOnlineCache().eq(false).offlineOnly()
                    .byOrgUnits().eq(ou)
                    .byProgram().eq(program)
                    .blockingGet()
                    .flatMap { tei ->
                        listOf(tei)
                    }.map { tei ->
                        transform(tei, program)
                    }
            }
        }

    override suspend fun event(
        program: String,
        programStage: String,
        dataElement: String,
        reasonDataElement: String?,
        teis: List<String>,
        date: String?
    ) =
        withContext(ioDispatcher) {
            return@withContext d2.eventModule().events()
                .byTrackedEntityInstanceUids(teis)
                .byProgramUid().eq(program)
                .byProgramStageUid().eq(programStage)
                .byEventDate().eq(
                    if (date != null) {
                        Date.valueOf(date)
                    } else {
                        DateUtils.getInstance().today
                    }
                )
                .withTrackedEntityDataValues()
                .blockingGet()
                .map {
                    eventTransform(it, dataElement, reasonDataElement)
                }
        }

    private fun eventTransform(
        event: Event,
        dataElement: String,
        reasonDataElement: String?,
    ): Attendance? {
        val dataValue = event.trackedEntityDataValues()?.find { it.dataElement() == dataElement }
        val reason = event.trackedEntityDataValues()?.find { it.dataElement() == reasonDataElement }

        return if (dataValue != null) {
            val tei = d2.enrollmentModule().enrollments()
                .byUid().eq(event.enrollment().toString())
                .one().blockingGet()?.trackedEntityInstance()

            Attendance(
                tei = tei.toString(),
                dataElement = dataElement,
                value = dataValue.value().toString(),
                reasonDataElement = if (reason == null) {
                    null
                } else {
                    reasonDataElement
                },
                reasonOfAbsence = reason?.value(),
                date = DateUtil.formatDate(
                    event.eventDate()?.time ?: DateUtils.getInstance().today.time
                ).toString()
            )
        } else null
    }

    override suspend fun getOptions(dataElement: String): MutableList<Option>? =
        withContext(ioDispatcher) {
            val result = d2.dataElementModule().dataElements()
                .byUid().eq(dataElement).one()
                .blockingGet()

            return@withContext if (result != null) {
                val optionSet = d2.optionModule().optionSets()
                    .byUid().eq(result.optionSet()?.uid()).one()
                    .blockingGet().uid()

                d2.optionModule().options().byOptionSetUid().eq(optionSet).blockingGet()
            } else null
        }

    override suspend fun getProgramStage(uid: String): ProgramStage =
        withContext(ioDispatcher) {
            return@withContext d2.programModule().programStages()
                .byUid().eq(uid).one().blockingGet()
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
