package com.arjun.todo.views.targetdetail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arjun.todo.data.Target
import com.arjun.todo.data.TargetDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelTargetDetail @ViewModelInject constructor(
    private val targetDao: TargetDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {
    private val targetFlow = targetDao.getTarget(state.get<Target>("target")!!.id)
    val target = targetFlow.asLiveData()

    fun onPrimaryButtonClicked() = viewModelScope.launch {
        val target = target.value!!

        if (target.isInProgress) {
            targetDao.update(target.copy(progress = target.currentProgress, beginTimestamp = -1))
        } else {
            withContext(Dispatchers.IO) {
                targetDao.getActiveTargets().forEach { activeTarget ->
                    targetDao.update(activeTarget.copy(progress = activeTarget.currentProgress, beginTimestamp = -1))
                }
                targetDao.update(target.copy(beginTimestamp = System.currentTimeMillis()))
            }
            targetDao.update(target.copy(beginTimestamp = System.currentTimeMillis()))
        }
    }

    private val targetDetailEventChannel = Channel<TargetDetailEvent>()
    val targetDetailEvent = targetDetailEventChannel.receiveAsFlow()

    fun onEditButtonClicked(target: Target) = viewModelScope.launch {
        targetDetailEventChannel.send(TargetDetailEvent.NavigateToEditTarget(target))
    }

    sealed class TargetDetailEvent {
        data class NavigateToEditTarget(val target: Target) : TargetDetailEvent()
    }
}