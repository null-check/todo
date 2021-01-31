package com.arjun.todo.views.targets

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arjun.todo.data.*
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

    private val targetsEventChannel = Channel<ViewModelTargets.TargetsEvent>()
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

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    sealed class TargetsEvent {
//        object NavigateToAddTaskScreen : TasksEvent()
//        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
    }
}