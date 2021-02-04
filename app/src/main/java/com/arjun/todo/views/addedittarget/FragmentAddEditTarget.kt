package com.arjun.todo.views.addedittarget

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arjun.todo.R
import com.arjun.todo.databinding.FragmentAddEditTargetBinding
import com.arjun.todo.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FragmentAddEditTarget : Fragment(R.layout.fragment_add_edit_target) {

    private val viewModel: ViewModelAddEditTarget by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTargetBinding.bind(view)

        binding.apply {
            etTargetName.setText(viewModel.targetName)
            tvDateCreated.isVisible = viewModel.target != null
            etTargetHours.setText(viewModel.targetHours.toString())
            etTargetMinutes.setText(viewModel.targetMins.toString())
            tvDateCreated.text = "Created: ${viewModel.target?.createdDateFormatted}"
            weeklyDailyToggle.isChecked = viewModel.isDaily

            etTargetName.addTextChangedListener {
                viewModel.targetName = it.toString()
            }

            etTargetHours.addTextChangedListener {
                viewModel.targetHours = if (!it.isNullOrBlank()) it.toString().toInt() else 0
            }

            etTargetMinutes.addTextChangedListener {
                viewModel.targetMins = if (!it.isNullOrBlank()) it.toString().toInt() else 0
            }

            weeklyDailyToggle.setOnClickListener {
                viewModel.isDaily = weeklyDailyToggle.isChecked
            }

            fabSaveTarget.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTargetEvent.collect { event ->
                when (event) {
                    is ViewModelAddEditTarget.AddEditTargetEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    is ViewModelAddEditTarget.AddEditTargetEvent.NavigateBackWithResult -> {
                        setFragmentResult(
                            "add_edit_target_request",
                            bundleOf("add_edit_target_result" to event.result
                            )
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }
}