package com.arjun.todo.views.tasks

import androidx.lifecycle.*
import androidx.recyclerview.widget.ItemTouchHelper
import com.arjun.todo.data.PreferencesManager
import com.arjun.todo.data.SortOrder
import com.arjun.todo.data.Task
import com.arjun.todo.data.TaskDao
import com.arjun.todo.views.ADD_TASK_RESULT_OK
import com.arjun.todo.views.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelTasks @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {

    val searchQueryFlow = state.getLiveData<String>("searchQuery", "")
    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
        searchQueryFlow.asFlow(),
        preferencesFlow
    ) { searchQuery, preferences ->
        Pair(searchQuery, preferences)
    }.flatMapLatest { (searchQuery, preferences) ->
        taskDao.getTasks(searchQuery, preferences.sortOrder, preferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedToggled(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = checked))
    }

    fun onTaskSwiped(task: Task, direction: Int) = viewModelScope.launch {
        when (direction) {
            ItemTouchHelper.LEFT -> {
                taskDao.delete(task)
                tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
            }
            ItemTouchHelper.RIGHT -> {
                taskDao.update(task.copy(completed = !task.completed))
            }
        }
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClicked() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(message: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedMessage(message))
    }

    fun onDeleteCompletedClicked() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToDeleteCompletedScreen)
    }

    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class ShowTaskSavedMessage(val message: String) : TasksEvent()
        object NavigateToDeleteCompletedScreen : TasksEvent()
    }
}