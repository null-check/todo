package com.arjun.todo.views.deletecompleted

import androidx.lifecycle.ViewModel
import com.arjun.todo.data.TaskDao
import com.arjun.todo.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelDeleteCompleted @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {
    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompleted()
    }
}