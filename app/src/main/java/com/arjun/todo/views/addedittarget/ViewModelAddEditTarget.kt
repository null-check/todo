package com.arjun.todo.views.addedittarget

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjun.todo.data.Target
import com.arjun.todo.data.TargetDao
import com.arjun.todo.data.TargetPeriod
import com.arjun.todo.views.ADD_TASK_RESULT_OK
import com.arjun.todo.views.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ViewModelAddEditTarget @ViewModelInject constructor(
    private val targetDao: TargetDao,
    @Assisted private val state: SavedStateHandle
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

        val targetAmount = 60 * targetHours + targetMins
        if (targetAmount <= 0) {
            showInvalidInputMessage("Target has to be at least 1 minute")
        }

        if (target != null) {
            updateTarget(target.copy(name = targetName,
                targetAmount = targetAmount,
                period = (if (isDaily) TargetPeriod.DAILY else TargetPeriod.WEEKLY).name,
                progress = target.currentProgress))
        } else {
            createTarget(Target(name = targetName,
                targetAmount = targetAmount,
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