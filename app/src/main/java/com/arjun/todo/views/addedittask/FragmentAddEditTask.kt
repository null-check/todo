package com.arjun.todo.views.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arjun.todo.R
import com.arjun.todo.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddEditTask : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: ViewModelAddEditTask by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            etTaskName.setText(viewModel.taskName)
            tvDateCreated.isVisible = viewModel.task != null
            tvDateCreated.text = "Created: ${viewModel.task?.createdDateFormatted}"
        }
    }
}