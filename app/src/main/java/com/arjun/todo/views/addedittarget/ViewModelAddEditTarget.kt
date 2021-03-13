package com.arjun.todo.views.addedittarget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjun.todo.data.Target
import com.arjun.todo.data.TargetDao
import com.arjun.todo.data.TargetPeriod
import com.arjun.todo.views.ADD_TASK_RESULT_OK
import com.arjun.todo.views.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelAddEditTarget @Inject constructor(
    private val targetDao: TargetDao,
    private val state: SavedStateHandle
) : ViewModel() {
    val target = state.get<Target>("target")

    var targetName = state.get<String>("targetName") ?: target?.name ?: ""
        set(value) {
            field = value
            state.set("targetName", value)
        }

    var targetHours = state.get<Int>("targetHours") ?: target?.targetHours ?: 4
        set(value) {
            field = value
            state.set("targetHours", value)
        }

    var targetMins = state.get<Int>("targetMins") ?: target?.targetMins ?: 0
        set(value) {
            field = value
            state.set("targetMins", value)
        }

    var isDaily = state.get<Boolean>("isDaily") ?: target?.period == TargetPeriod.DAILY.name
        set(value) {
            field = value
            state.set("isDaily", value)
        }

    fun onSaveClick() {
        if (targetName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        val targetInMins = 60 * targetHours + targetMins
        val targetInSecs = 60 * targetInMins
        if (targetInMins <= 0) {
            showInvalidInputMessage("Target has to be at least 1 minute")
        }

        if (target != null) {
            updateTarget(target.copy(name = targetName,
                targetAmount = targetInSecs,
                period = (if (isDaily) TargetPeriod.DAILY else TargetPeriod.WEEKLY).name,
                progress = target.currentProgress))
        } else {
            createTarget(Target(name = targetName,
                targetAmount = targetInSecs,
                period = (if (isDaily) TargetPeriod.DAILY else TargetPeriod.WEEKLY).name))
        }
    }

    private val addEditTargetEventChannel = Channel<AddEditTargetEvent>()
    val addEditTargetEvent = addEditTargetEventChannel.receiveAsFlow()

    private fun createTarget(target: Target) = viewModelScope.launch {
        targetDao.insert(target)
        addEditTargetEventChannel.send(AddEditTargetEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTarget(target: Target) = viewModelScope.launch {
        targetDao.update(target)
        addEditTargetEventChannel.send(AddEditTargetEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(message: String) = viewModelScope.launch {
        addEditTargetEventChannel.send(AddEditTargetEvent.ShowInvalidInputMessage(message))
    }

    sealed class AddEditTargetEvent {
        data class ShowInvalidInputMessage(val message: String) : AddEditTargetEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTargetEvent()
    }
}