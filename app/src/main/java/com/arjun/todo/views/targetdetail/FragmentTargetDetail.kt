package com.arjun.todo.views.targetdetail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arjun.todo.R
import com.arjun.todo.databinding.FragmentTargetDetailBinding
import com.arjun.todo.util.exhaustive
import com.arjun.todo.util.getSecsFormatted
import com.arjun.todo.views.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class FragmentTargetDetail : Fragment(R.layout.fragment_target_detail) {

    private val viewModel: ViewModelTargetDetail by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTargetDetailBinding.bind(view)

        binding.apply {
            editButton.setOnClickListener {
                viewModel.onEditButtonClicked(viewModel.target.value!!)
            }

            primaryButton.setOnClickListener {
                viewModel.onPrimaryButtonClicked()
            }
        }

        viewModel.target.observe(viewLifecycleOwner) { target ->
            binding.apply {
                tvTargetAmount.text = resources.getString(R.string.target_amount_text, getSecsFormatted(target.targetAmount))
                tvTargetPeriod.text = target.period.capitalize(Locale.getDefault())
                tvProgressIndicator.isInvisible = !target.isInProgress
                tvTargetProgressPercent.text = "${target.progressPercent}%"
                targetProgressBar.progress = target.progressPercent
                tvTargetDone.text = resources.getString(R.string.target_progress_text, getSecsFormatted(target.currentProgress))
                tvTargetRemaining.text = resources.getString(R.string.target_remaining_text, getSecsFormatted(target.remainingAmount))

                primaryButton.text = if (viewModel.target.value!!.isInProgress) "End session" else "Begin session"
                primaryButton.apply {
                    if (viewModel.target.value!!.isInProgress) {
                        text = "End session"
                        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                    } else {
                        text = "Begin session"
                        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.leaf_green))
                    }
                }
            }
            (activity as MainActivity?)?.supportActionBar?.title = target.name // Todo better way to update title?
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.targetDetailEvent.collect { event ->
                when (event) {
                    is ViewModelTargetDetail.TargetDetailEvent.NavigateToEditTarget -> {
                        val action = FragmentTargetDetailDirections.actionFragmentTargetDetailToFragmentAddEditTarget(target = event.target, title = "Edit target")
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }
}