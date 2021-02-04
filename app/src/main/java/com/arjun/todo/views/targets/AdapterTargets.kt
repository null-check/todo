package com.arjun.todo.views.targets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arjun.todo.R
import com.arjun.todo.data.Target
import com.arjun.todo.databinding.ItemTargetBinding
import com.arjun.todo.util.getMinsFormatted
import kotlin.math.max

class AdapterTargets(private val targetClickListener: OnTargetClickListener) :
    ListAdapter<Target, AdapterTargets.ViewHolderTarget>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTarget {
        val binding = ItemTargetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderTarget(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderTarget, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolderTarget constructor(private val binding: ItemTargetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        targetClickListener.onItemClick(getItem(adapterPosition))
                    }
                }
            }
        }

        fun bind(target: Target) {
            binding.apply {
                tvName.text = target.name
                tvProgressIndicator.isVisible = target.isInProgress
                tvTargetAmount.text = itemView.context.getString(R.string.target_amount_text, getMinsFormatted(target.targetAmount))
                tvTargetProgress.text = itemView.context.getString(R.string.target_progress_text, getMinsFormatted(target.currentProgress))
                tvTargetRemaining.text = itemView.context.getString(R.string.target_remaining_text, getMinsFormatted(target.remainingAmount))
                (bgProgress.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = target.progressPercent.toFloat() / 100
                bgProgress.setBackgroundColor(ContextCompat.getColor(itemView.context, if (target.isDone) R.color.progress_green else R.color.baby_blue))
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Target>() {
        override fun areItemsTheSame(oldItem: Target, newItem: Target): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Target, newItem: Target): Boolean =
            oldItem == newItem

    }

    interface OnTargetClickListener {
        fun onItemClick(target: Target)
    }
}