package com.arjun.todo.views.targets

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.todo.R
import com.arjun.todo.data.Target
import com.arjun.todo.databinding.FragmentTargetsBinding
import com.arjun.todo.util.exhaustive
import com.arjun.todo.views.targets.AdapterTargets.OnTargetClickListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FragmentTargets : Fragment(R.layout.fragment_targets) {

    private val viewModel: ViewModelTargets by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterTargets = AdapterTargets(object : OnTargetClickListener {
            override fun onItemClick(target: Target) {
                viewModel.onTargetSelected(target)
            }
        })

        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)

        val binding = FragmentTargetsBinding.bind(view)

        binding.apply {
            recyclerViewTargets.apply {
                adapter = adapterTargets
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(dividerItemDecoration)
            }

            fabAddTarget.setOnClickListener {
                viewModel.onAddNewTargetClicked()
            }
        }

        setFragmentResultListener("add_edit_target_request") { _, bundle ->
            val result = bundle.getInt("add_edit_target_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.targets.observe(viewLifecycleOwner) { adapterTargets.submitList(it) }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.targetsEvent.collect { event ->
                when (event) {
                    is ViewModelTargets.TargetsEvent.NavigateToAddTargetScreen -> {
                        val action = FragmentTargetsDirections.actionFragmentTargetsToFragmentAddEditTarget(title = "Add target")
                        findNavController().navigate(action)
                    }
                    is ViewModelTargets.TargetsEvent.NavigateToTargetDetailScreen -> {
                        val action = FragmentTargetsDirections.actionFragmentTargetsToFragmentTargetDetail(target = event.target, title = event.target.name)
                        findNavController().navigate(action)
                    }
                    is ViewModelTargets.TargetsEvent.ShowTaskSavedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }
    }
}