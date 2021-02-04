package com.arjun.todo.views.targets

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arjun.todo.data.*
import com.arjun.todo.data.Target
import com.arjun.todo.views.ADD_TARGET_RESULT_OK
import com.arjun.todo.views.EDIT_TARGET_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ViewModelTargets @ViewModelInject constructor(
    private val targetDao: TargetDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQueryFlow = state.getLiveData<String>("searchQuery", "")
    val preferencesFlow = preferencesManager.preferencesFlow

    private val targetsEventChannel = Channel<TargetsEvent>()
    val targetsEvent = targetsEventChannel.receiveAsFlow()

    private val targetsFlow = combine(
        searchQueryFlow.asFlow(),
        preferencesFlow
    ) { searchQuery, preferences ->
        Pair(searchQuery, preferences)
    }.flatMapLatest { (searchQuery, preferences) ->
        targetDao.getTargets(searchQuery, preferences.sortOrder)
    }

    val targets = targetsFlow.asLiveData()

    // Todo
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onTargetSelected(target: Target) = viewModelScope.launch {
        targetsEventChannel.send(TargetsEvent.NavigateToTargetDetailScreen(target))
    }

    fun onAddNewTargetClicked() = viewModelScope.launch {
        targetsEventChannel.send(TargetsEvent.NavigateToAddTargetScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TARGET_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TARGET_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(message: String) = viewModelScope.launch {
        targetsEventChannel.send(TargetsEvent.ShowTaskSavedMessage(message))
    }

    sealed class TargetsEvent {
        object NavigateToAddTargetScreen : TargetsEvent()
        data class NavigateToTargetDetailScreen(val target: Target) : TargetsEvent()
        data class ShowTaskSavedMessage(val message: String) : TargetsEvent()
    }
}