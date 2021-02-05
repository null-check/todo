package com.arjun.todo.views.tasks

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
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
import com.arjun.todo.data.SortOrder
import com.arjun.todo.data.Task
import com.arjun.todo.databinding.FragmentTasksBinding
import com.arjun.todo.util.exhaustive
import com.arjun.todo.util.onQueryTextChanged
import com.arjun.todo.views.ItemSwipeCallback
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "FragmentTasks"

@AndroidEntryPoint
class FragmentTasks : Fragment(R.layout.fragment_tasks) {

    private val viewModel: ViewModelTasks by viewModels()

    private lateinit var searchView: SearchView

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

        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)

        val binding = FragmentTasksBinding.bind(view)

        val checkIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_24)!!
        val uncheckIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_replay_24)!!
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)!!
        val greenBg = ColorDrawable(resources.getColor(R.color.leaf_green))
        val yellowBg = ColorDrawable(resources.getColor(R.color.sun_yellow))
        val redBg = ColorDrawable(resources.getColor(R.color.red))

        binding.apply {
            recyclerViewTasks.apply {
                adapter = adapterTasks
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
                addItemDecoration(dividerItemDecoration)

                ItemTouchHelper(object : ItemSwipeCallback(deleteIcon, redBg) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val task = adapterTasks.currentList[viewHolder.adapterPosition]
                        viewModel.onTaskSwiped(task, direction)
                    }

                    override fun getIconDrawable(viewHolder: RecyclerView.ViewHolder, direction: Int): Drawable? {
                        return if (direction == ItemTouchHelper.RIGHT && viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                            val task = adapterTasks.currentList[viewHolder.adapterPosition]
                            if (task.completed xor isAnimating) {
                                uncheckIcon
                            } else {
                                checkIcon
                            }
                        } else {
                            super.getIconDrawable(viewHolder, direction)
                        }
                    }

                    override fun getBackgroundDrawable(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int
                    ): Drawable? {
                        return if (direction == ItemTouchHelper.RIGHT && viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                            val task = adapterTasks.currentList[viewHolder.adapterPosition]
                            if (task.completed xor isAnimating) {
                                yellowBg
                            } else {
                                greenBg
                            }
                        } else {
                            super.getBackgroundDrawable(viewHolder, direction)
                        }
                    }
                }).attachToRecyclerView(recyclerViewTasks)
            }

            fabAddTask.setOnClickListener {
                viewModel.onAddNewTaskClicked()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.tasks.observe(viewLifecycleOwner) { adapterTasks.submitList(it) }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is ViewModelTasks.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.task)
                            }.show()
                    }
                    is ViewModelTasks.TasksEvent.NavigateToAddTaskScreen -> {
                        val action = FragmentTasksDirections.actionTasksFragmentToAddEditTaskFragment(null, "New Task")
                        findNavController().navigate(action)
                    }
                    is ViewModelTasks.TasksEvent.NavigateToEditTaskScreen -> {
                        val action = FragmentTasksDirections.actionTasksFragmentToAddEditTaskFragment(event.task, "Edit Task")
                        findNavController().navigate(action)
                    }
                    is ViewModelTasks.TasksEvent.ShowTaskSavedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is ViewModelTasks.TasksEvent.NavigateToDeleteCompletedScreen -> {
                        val action = FragmentTasksDirections.actionGlobalDeleteCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQueryFlow.value
        if (pendingQuery!!.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            viewModel.searchQueryFlow.value = it
        }
        searchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (viewModel.searchQueryFlow.value.isNullOrEmpty()) {
                searchItem.collapseActionView()
            }
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
                viewModel.onDeleteCompletedClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null) // To fix bug where it sends empty string on destroy
    }
}