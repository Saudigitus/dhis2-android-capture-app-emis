package org.saudigitus.emis.ui.attendance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dhis2.commons.Constants.SERVER
import org.dhis2.commons.Constants.USER
import org.dhis2.commons.data.SearchTeiModel
import org.dhis2.commons.date.DateUtils
import org.dhis2.commons.prefs.PreferenceProvider
import org.dhis2.commons.resources.ResourceManager
import org.hisp.dhis.android.core.option.Option
import org.saudigitus.emis.R
import org.saudigitus.emis.data.local.AppConfigManager
import org.saudigitus.emis.data.local.DataManager
import org.saudigitus.emis.data.model.AppConfig
import org.saudigitus.emis.data.model.Attendance
import org.saudigitus.emis.data.model.AttendanceLineList
import org.saudigitus.emis.data.model.FilterSettings
import org.saudigitus.emis.data.remote.DataStoreConfig
import org.saudigitus.emis.service.Basic64AuthInterceptor
import org.saudigitus.emis.ui.components.model.AttendanceActions
import org.saudigitus.emis.utils.Constants
import org.saudigitus.emis.utils.Constants.ABSENT
import org.saudigitus.emis.utils.Constants.ACTION_SORT_FIRST
import org.saudigitus.emis.utils.Constants.ACTION_SORT_SECOND
import org.saudigitus.emis.utils.Constants.ACTION_SORT_THIRD
import org.saudigitus.emis.utils.Constants.FILTER_INTENT_KEY
import org.saudigitus.emis.utils.Constants.GREEN
import org.saudigitus.emis.utils.Constants.KEY_DATA_ELEMENT
import org.saudigitus.emis.utils.Constants.KEY_PROGRAM_STAGE
import org.saudigitus.emis.utils.Constants.KEY_REASON_DATA_ELEMENT
import org.saudigitus.emis.utils.Constants.LATE
import org.saudigitus.emis.utils.Constants.ORANGE
import org.saudigitus.emis.utils.Constants.PRESENT
import org.saudigitus.emis.utils.Constants.RED
import org.saudigitus.emis.utils.Constants.USER_PASS
import org.saudigitus.emis.utils.Constants.WHITE
import org.saudigitus.emis.utils.DateUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@HiltViewModel
class AttendanceViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataManager: DataManager,
    private val appConfig: AppConfigManager,
    private val preferenceProvider: PreferenceProvider,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private val _attendanceSetting = MutableStateFlow(AttendanceLineList())
    private val attendanceSetting = _attendanceSetting.asStateFlow()

    private val _searchTeiModel = MutableStateFlow<List<SearchTeiModel>?>(null)
    val searchTeiModel = _searchTeiModel.asStateFlow()

    private val _reasonOfAbsence = MutableStateFlow<List<Option>?>(null)
    val reasonOfAbsence = _reasonOfAbsence.asStateFlow()

    private val _attendanceOptions = MutableStateFlow<MutableList<AttendanceActions>?>(null)
    val attendanceActions = _attendanceOptions.asStateFlow()

    private val _attendanceList = MutableStateFlow<List<Attendance?>?>(null)
    val attendanceList = _attendanceList.asStateFlow()

    private val _attendanceUiState = MutableStateFlow(mutableListOf(AttendanceUiState()))
    val attendanceBtnState = _attendanceUiState.asStateFlow()
    private var attendanceUiState = mutableListOf(AttendanceUiState())

    private val _attendanceStep = MutableStateFlow(ButtonStep.EDITING)
    val attendanceStep = _attendanceStep.asStateFlow()

    private val _eventDate = MutableStateFlow("")
    private val eventDate = _eventDate.asStateFlow()

    private val _theme = MutableStateFlow(0)
    val theme = _theme.asStateFlow()

    private val attendanceCache = mutableSetOf<Attendance>()

    init {
        val filter = savedStateHandle.get<FilterSettings>(FILTER_INTENT_KEY)
        _theme.value = filter?.theme ?: R.style.AppTheme
        _attendanceSetting.update { attendance ->
            attendance.copy(program = filter?.program.toString())
        }

        authenticate()

        getTeis(
            ou = filter?.ou.toString(),
            program = filter?.program.toString()
        )
        loadConfig(filter?.program.toString())
        attendanceEvents()
    }

    fun saveConfig(config: AppConfig?) {
        viewModelScope.launch {
            if (config != null) {
                appConfig.save(config)
            }
        }
    }

    fun server() = preferenceProvider.getString(SERVER)

    fun config(dataStoreConfig: DataStoreConfig) {
        viewModelScope.launch {
            if (attendanceSetting.value.programStage.isNullOrEmpty()) {
                downloadAndStoreConfig(dataStoreConfig)
            }
        }
    }

    private fun authenticate() {
        Basic64AuthInterceptor.setCredential(
            username = preferenceProvider.getString(USER).toString(),
            password = preferenceProvider.getString(USER_PASS).toString()
        )
    }

    private fun loadConfig(program: String) {
        viewModelScope.launch {
            val config = appConfig.getAppConfigByProgram(program).firstOrNull()
            withContext(Dispatchers.Main) {
                if (config != null) {
                    composeLineList(config)
                }
            }
        }
    }

    private fun composeLineList(appConfig: AppConfig) {
        appConfig.linelist?.forEach {
            when (it.objectType) {
                Constants.KEY_ATTENDANCE, Constants.KEY_SPROGRAM_STAGE -> {
                    val values = it.values

                    values?.forEach { value ->
                        if (Constants.KEY_ATTENDANCE == it.objectType) {
                            when (value.key) {
                                KEY_PROGRAM_STAGE -> {
                                    _attendanceSetting.update { attendance ->
                                        attendance.copy(programStage = value.value)
                                    }
                                }
                                KEY_DATA_ELEMENT -> {
                                    _attendanceSetting.update { attendance ->
                                        attendance.copy(dataElement = value.value)
                                    }
                                }
                                KEY_REASON_DATA_ELEMENT -> {
                                    _attendanceSetting.update { attendance ->
                                        attendance.copy(reasonDataElement = value.value)
                                    }
                                }
                            }
                        }
                    }
                }
                Constants.KEY_PROGRAM_STAGE_SECTION -> { }
                KEY_DATA_ELEMENT -> { }
            }
        }

        getReasonForAbsence(attendanceSetting.value.reasonDataElement.toString())
        getAttendanceOptions(attendanceSetting.value.dataElement.toString())
    }

    private fun getTeis(ou: String, program: String) {
        viewModelScope.launch {
            _searchTeiModel.value = dataManager.trackedEntityInstances(ou, program)
        }
    }

    private fun getReasonForAbsence(dataElement: String) {
        viewModelScope.launch {
            _reasonOfAbsence.value = dataManager.getOptions(dataElement)
        }
    }

    private fun getAttendanceOptions(dataElement: String) {
        viewModelScope.launch {
            _attendanceOptions.value = dataManager.getOptions(dataElement)?.map {
                when (it.code().toString().lowercase()) {
                    PRESENT -> {
                        AttendanceActions(
                            code = it.code().toString(),
                            name = it.displayName().toString(),
                            dataElement = dataElement,
                            icon = resourceManager.getObjectStyleDrawableResource(
                                null,
                                R.drawable.ic_present
                            ),
                            hexColor = GREEN,
                            actionOrder = ACTION_SORT_FIRST
                        )
                    }
                    LATE -> {
                        AttendanceActions(
                            code = it.code().toString(),
                            name = it.displayName().toString(),
                            dataElement = dataElement,
                            icon = resourceManager.getObjectStyleDrawableResource(
                                null,
                                R.drawable.ic_late
                            ),
                            hexColor = ORANGE,
                            actionOrder = ACTION_SORT_SECOND
                        )
                    }
                    ABSENT -> {
                        AttendanceActions(
                            code = it.code().toString(),
                            name = it.displayName().toString(),
                            dataElement = dataElement,
                            icon = resourceManager.getObjectStyleDrawableResource(
                                null,
                                R.drawable.ic_absent
                            ),
                            hexColor = RED,
                            actionOrder = ACTION_SORT_THIRD
                        )
                    }
                    else -> {
                        AttendanceActions(
                            icon = resourceManager.getObjectStyleDrawableResource(
                                null,
                                R.drawable.ic_empty
                            )
                        )
                    }
                }
            }
                ?.sortedWith(compareBy { it.actionOrder })
                ?.toMutableList()
        }
    }

    fun setAttendanceStep(step: ButtonStep) {
        _attendanceStep.value = step
    }

    fun attendanceEvents(
        date: String? = DateUtil.formatDate(DateUtils.getInstance().today.time)
    ) {
        _eventDate.value = date.toString()
        viewModelScope.launch {
            searchTeiModel.collect {
                val teiKeys = it?.map { teiModel ->
                    teiModel.tei.uid()
                }

                _attendanceList.value = teiKeys?.let { uids ->
                    dataManager.event(
                        program = attendanceSetting.value.program.toString(),
                        programStage = attendanceSetting.value.programStage.toString(),
                        dataElement = attendanceSetting.value.dataElement.toString(),
                        teis = uids,
                        date = date.toString()
                    )
                }
                clearCache()
                attendanceList.value?.let { data ->
                    attendanceCache.addAll(data.requireNoNulls())
                }
                setInitialAttendanceStatus()
            }
        }
    }

    private fun setInitialAttendanceStatus() {
        attendanceUiState = attendanceList.value?.map { attendance ->
            attendanceUiMapper(
                index = attendanceActions.value?.indexOfFirst { it.code == attendance?.value} ?: 0,
                tei = attendance?.tei.toString(),
                attendanceValue = attendance?.value.toString()
            )
        }?.toMutableList() ?: mutableListOf(AttendanceUiState())

        _attendanceUiState.value = attendanceUiState
    }

    private fun getAttendanceUiState(
        index: Int,
        tei: String,
        value: String
    ): MutableList<AttendanceUiState> {
        val uiCacheItem = attendanceUiMapper(
            index = index,
            tei = tei,
            attendanceValue = value
        )

        val uiCache = attendanceUiState.find { it.btnId == tei }

        if (uiCache == null) {
            attendanceUiState.add(uiCacheItem)
        } else {
            attendanceUiState.remove(uiCache)
            attendanceUiState.add(uiCacheItem)
        }

        return attendanceUiState
    }

    fun setAttendance(
        index: Int,
        ou: String,
        tei: String,
        value: String,
        reasonOfAbsence: String? = null
    ) {
        _attendanceUiState.value = getAttendanceUiState(index, tei, value)

        val attendance = Attendance(
            tei = tei,
            dataElement = attendanceSetting.value.dataElement.toString(),
            value = value,
            reasonDataElement = attendanceSetting.value.reasonDataElement.toString(),
            reasonOfAbsence = reasonOfAbsence,
            date = eventDate.value
        )

        val cacheItem = attendanceCache.find { it.tei == attendance.tei }

        if (cacheItem == null) {
            attendanceCache.add(attendance)
        } else {
            attendanceCache.remove(cacheItem)
            attendanceCache.add(attendance)
        }

        viewModelScope.launch {
            dataManager.save(
                ou = ou,
                program = attendanceSetting.value.program.toString(),
                programStage = attendanceSetting.value.programStage.toString(),
                attendance = attendance
            )
        }
    }

    fun getSummary(
        summaryData: (String, String, String) -> Unit
    ) {
        val presence = "${attendanceCache.filter { it.value == PRESENT }.size}"
        val late = "${attendanceCache.filter { it.value == LATE }.size}"
        val absent = "${attendanceCache.filter { it.value == ABSENT }.size}"

        summaryData(presence, late, absent)
    }

    private fun attendanceUiMapper(
        index: Int,
        tei: String,
        attendanceValue: String
    ) = AttendanceUiState(
        btnIndex = index,
        btnId = tei,
        iconTint = 0,
        buttonState = AttendanceButtonState(
            buttonType = when (attendanceValue) {
                PRESENT -> { ButtonType.PRESENT }
                LATE -> { ButtonType.LATE }
                ABSENT -> { ButtonType.ABSENT }
                else -> null
            },
            containerColor = when (attendanceValue) {
                PRESENT -> { GREEN }
                LATE -> { ORANGE }
                ABSENT -> { RED }
                else -> null
            },
            contentColor = WHITE
        )
    )

    private fun downloadAndStoreConfig(dataStoreConfig: DataStoreConfig) {
        dataStoreConfig.getConfig("${attendanceSetting.value.program}")
            .enqueue(object : Callback<AppConfig> {
                override fun onResponse(
                    call: Call<AppConfig>,
                    response: Response<AppConfig>
                ) {
                    saveConfig(response.body())
                }

                override fun onFailure(call: Call<AppConfig>, t: Throwable) {
                    call.cancel()
                    Timber.tag("CONF").e(t)
                }
            })

    }

    fun clearCache() {
        attendanceCache.clear()
        attendanceUiState.clear()
    }
}
