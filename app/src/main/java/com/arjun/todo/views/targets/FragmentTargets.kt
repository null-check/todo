package com.arjun.todo.views.targets

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arjun.todo.R
import com.arjun.todo.data.Target
import com.arjun.todo.databinding.FragmentTargetsBinding
import com.arjun.todo.util.exhaustive
import com.arjun.todo.views.ItemSwipeCallback
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

        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            LinearLayoutManager.VERTICAL
        )
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.divider
            )!!
        )

        val binding = FragmentTargetsBinding.bind(view)

        val startIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_play_arrow_24)!!
        val stopIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_pause_24)!!
        val greenBg = ColorDrawable(resources.getColor(R.color.leaf_green))
        val yellowBg = ColorDrawable(resources.getColor(R.color.sun_yellow))

        binding.apply {
            recyclerViewTargets.apply {
                adapter = adapterTargets
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(dividerItemDecoration)

                ItemTouchHelper(object : ItemSwipeCallback(startIcon, greenBg) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val target = adapterTargets.currentList[viewHolder.adapterPosition]
                        viewModel.onTargetSwiped(target)
                    }

                    override fun getIconDrawable(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int
                    ): Drawable? {
                        return if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                            val target = adapterTargets.currentList[viewHolder.adapterPosition]
                            if (target.isInProgress xor isAnimating) {
                                stopIcon
                            } else {
                                startIcon
                            }
                        } else {
                            null
                        }
                    }

                    override fun getBackgroundDrawable(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int
                    ): Drawable? {
                        return if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                            val target = adapterTargets.currentList[viewHolder.adapterPosition]
                            if (target.isInProgress xor isAnimating) {
                                yellowBg
                            } else {
                                greenBg
                            }
                        } else {
                            null
                        }
                    }
                }).attachToRecyclerView(recyclerViewTargets)
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
                        val action =
                            FragmentTargetsDirections.actionFragmentTargetsToFragmentAddEditTarget(
                                title = "Add target"
                            )
                        findNavController().navigate(action)
                    }
                    is ViewModelTargets.TargetsEvent.NavigateToTargetDetailScreen -> {
                        val action =
                            FragmentTargetsDirections.actionFragmentTargetsToFragmentTargetDetail(
                                target = event.target,
                                title = event.target.name
                            )
                        findNavController().navigate(action)
                    }
                    is ViewModelTargets.TargetsEvent.ShowTargetSavedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is ViewModelTargets.TargetsEvent.UpdateTargetProgress -> {
                        adapterTargets.notifyItemChanged(adapterTargets.currentList.indexOf(event.target))
                    }
                }.exhaustive
            }
        }
    }
}