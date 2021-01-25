package com.arjun.todo.views.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arjun.todo.data.Task
import com.arjun.todo.databinding.ItemTaskBinding

class AdapterTasks(private val taskClickListener: OnItemClickListener) :
    ListAdapter<Task, AdapterTasks.ViewHolderTask>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTask {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderTask(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderTask, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolderTask constructor(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        taskClickListener.onItemClick(getItem(adapterPosition))
                    }
                }
                checkbox.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        taskClickListener.onCheckboxClick(
                            getItem(adapterPosition),
                            checkbox.isChecked
                        )
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                checkbox.isChecked = task.completed
                tvName.text = task.name
                tvName.paint.isStrikeThruText = task.completed
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem

    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckboxClick(task: Task, isChecked: Boolean)
    }
}