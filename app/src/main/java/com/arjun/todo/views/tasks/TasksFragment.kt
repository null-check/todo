package com.arjun.todo.views.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.todo.R
import com.arjun.todo.data.SortOrder
import com.arjun.todo.data.Task
import com.arjun.todo.databinding.FragmentTasksBinding
import com.arjun.todo.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: ViewModelTasks by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterTasks = AdapterTasks(object : AdapterTasks.OnItemClickListener {
            override fun onItemClick(task: Task) {
                viewModel.onTaskSelected(task)
            }

            override fun onCheckboxClick(task: Task, isChecked: Boolean) {
                viewModel.onTaskCheckedChanged(task, isChecked)
            }
        })

        val binding = FragmentTasksBinding.bind(view)

        binding.apply {
            recyclerViewTasks.apply {
                adapter = adapterTasks
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) { adapterTasks.submitList(it) }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQueryFlow.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedToggled(item.isChecked)
                true
            }
            R.id.action_delete_completed -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}