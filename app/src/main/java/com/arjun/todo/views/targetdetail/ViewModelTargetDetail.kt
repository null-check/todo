package com.arjun.todo.views.targetdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arjun.todo.data.Target
import com.arjun.todo.data.TargetDao
import com.arjun.todo.time.alarm.AlarmBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelTargetDetail @Inject constructor(
    private val targetDao: TargetDao,
    private val alarmBuilder: AlarmBuilder,
    private val state: SavedStateHandle
) : ViewModel() {
    private val targetFlow = targetDao.getTarget(state.get<Target>("target")!!.id)
    val target = targetFlow.asLiveData()

    fun onPrimaryButtonClicked() = viewModelScope.launch {
        val target = target.value!!

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
        if (!target.isDone) {
            alarmBuilder.setTargetFinishAlarm(target)
        }
    }

    private fun pauseTarget(target: Target) = viewModelScope.launch {
        targetDao.update(target.copy(progress = target.currentProgress, beginTimestamp = -1))
        alarmBuilder.cancelLastAlarm()
    }

    private val targetDetailEventChannel = Channel<TargetDetailEvent>()
    val targetDetailEvent = targetDetailEventChannel.receiveAsFlow()

    fun onEditButtonClicked(target: Target) = viewModelScope.launch {
        targetDetailEventChannel.send(TargetDetailEvent.NavigateToEditTarget(target))
    }

    fun onResetButtonClicked(target: Target) = viewModelScope.launch {
        targetDao.update(target.copy(progress = 0, beginTimestamp = -1))
    }

    sealed class TargetDetailEvent {
        data class NavigateToEditTarget(val target: Target) : TargetDetailEvent()
    }
}