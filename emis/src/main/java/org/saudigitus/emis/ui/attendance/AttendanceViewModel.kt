package org.saudigitus.emis.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.dhis2.commons.data.SearchTeiModel
import org.saudigitus.emis.data.local.DataManager
import org.saudigitus.emis.data.local.AppConfigManager
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel
@Inject constructor(
    private val dataManager: DataManager,
    private val appConfig: AppConfigManager
): ViewModel() {

    private val _searchTeiModel = MutableStateFlow<List<SearchTeiModel>?>(null)
    val searchTeiModel = _searchTeiModel.asStateFlow()

    init {
        getTeis("AkDQ4JvN3dW")
    }

    fun enableEMIS(program: String) {
        viewModelScope.launch {
            appConfig.isConfigNull(program)
        }
    }

    fun getTeis(program: String) {
        viewModelScope.launch {
            _searchTeiModel.value = dataManager.trackedEntityInstances(program)
        }
    }
}