package com.arjun.todo.views.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arjun.todo.data.PreferencesManager
import com.arjun.todo.data.SortOrder
import com.arjun.todo.data.Task
import com.arjun.todo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ViewModelTasks @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQueryFlow = MutableStateFlow("")
    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksFlow = combine(
        searchQueryFlow,
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

    fun onTaskSelected(task: Task) {
        TODO("Not yet implemented")
    }

    fun onTaskCheckedChanged(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = checked))
    }

    fun deleteChecked() = viewModelScope.launch {
        taskDao.deleteCompleted()
    }
}