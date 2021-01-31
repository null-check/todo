package com.arjun.todo.views.targets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.todo.R
import com.arjun.todo.data.Target
import com.arjun.todo.databinding.FragmentTargetsBinding
import com.arjun.todo.views.targets.AdapterTargets.OnTargetClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentTargets : Fragment(R.layout.fragment_targets) {

    private val viewModel: ViewModelTargets by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterTargets = AdapterTargets(object : OnTargetClickListener {
            override fun onItemClick(target: Target) {
                // todo
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
//                viewModel.onAddNewTaskClicked()
            }
        }

        viewModel.targets.observe(viewLifecycleOwner) { adapterTargets.submitList(it) }
    }
}