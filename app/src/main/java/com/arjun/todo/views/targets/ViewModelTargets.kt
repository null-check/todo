package com.arjun.todo.views.targets

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arjun.todo.data.*
import com.arjun.todo.data.Target
import com.arjun.todo.views.ADD_TARGET_RESULT_OK
import com.arjun.todo.views.EDIT_TARGET_RESULT_OK
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class ViewModelTargets @ViewModelInject constructor(
    private val targetDao: TargetDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQueryFlow = state.getLiveData<String>("searchQuery", "")
    val preferencesFlow = preferencesManager.preferencesFlow

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
            ADD_TARGET_RESULT_OK -> showTargetSavedConfirmationMessage("Target added")
            EDIT_TARGET_RESULT_OK -> showTargetSavedConfirmationMessage("Target updated")
        }
    }

    private fun showTargetSavedConfirmationMessage(message: String) = viewModelScope.launch {
        targetsEventChannel.send(TargetsEvent.ShowTargetSavedMessage(message))
    }

    fun onTargetSwiped(target: Target) {
        if (target.isInProgress) {
            pauseTarget(target)
        } else {
            stopActiveTargets()
            startTarget(target)
        }
    }

    private fun stopActiveTargets() = viewModelScope.launch {
        targetDao.getActiveTargets().forEach { activeTarget ->
            targetDao.update(activeTarget.copy(progress = activeTarget.currentProgress, beginTimestamp = -1))
        }
    }

    private fun startTarget(target: Target) = viewModelScope.launch {
        targetDao.update(target.copy(beginTimestamp = System.currentTimeMillis()))
    }

    private fun pauseTarget(target: Target) = viewModelScope.launch {
        targetDao.update(target.copy(progress = target.currentProgress, beginTimestamp = -1))
    }

    private val targetsEventChannel = Channel<TargetsEvent>()
    val targetsEvent = targetsEventChannel.receiveAsFlow()

    sealed class TargetsEvent {
        object NavigateToAddTargetScreen : TargetsEvent()
        data class NavigateToTargetDetailScreen(val target: Target) : TargetsEvent()
        data class ShowTargetSavedMessage(val message: String) : TargetsEvent()
        data class UpdateTargetProgress(val target: Target) : TargetsEvent()
    }
}