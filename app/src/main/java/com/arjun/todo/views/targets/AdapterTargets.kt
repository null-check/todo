package com.arjun.todo.views.targets

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arjun.todo.R
import com.arjun.todo.data.Target
import com.arjun.todo.databinding.ItemTargetBinding
import com.arjun.todo.util.convertSecsToMillis
import com.arjun.todo.util.getSecsFormatted

class AdapterTargets(private val targetClickListener: OnTargetClickListener) :
    ListAdapter<Target, AdapterTargets.ViewHolderTarget>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTarget {
        val binding = ItemTargetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderTarget(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderTarget, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: ViewHolderTarget) {
        super.onViewRecycled(holder)
//        holder.countDownTimer?.cancel() TODO required?
    }

    override fun onViewDetachedFromWindow(holder: ViewHolderTarget) {
        super.onViewDetachedFromWindow(holder)
//        holder.countDownTimer?.cancel() TODO required?
    }

    inner class ViewHolderTarget constructor(private val binding: ItemTargetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var countDownTimer: CountDownTimer? = null

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
                tvTargetAmount.text = getSecsFormatted(target.targetAmount)
                updateProgress(target)
                bgProgress.setBackgroundColor(ContextCompat.getColor(itemView.context, if (target.isDone) R.color.progress_green else R.color.baby_blue))
            }

            if (target.isInProgress && !target.isDone) {
                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(convertSecsToMillis(target.remainingAmount), 1000) {
                    override fun onTick(p0: Long) {
                        updateProgress(target)
                    }

                    override fun onFinish() {
                        updateProgress(target)
                        binding.bgProgress.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.progress_green))
                    }
                }.start()
            } else {
                countDownTimer?.cancel()
            }
        }

        private fun updateProgress(target: Target) {
            binding.apply {
                tvTargetProgress.text = getSecsFormatted(target.currentProgress)
                if (target.isDone) {
                    tvTargetRemaining.text = itemView.context.getString(R.string.target_done_text)
                    tvTargetRemaining.setTextColor(ContextCompat.getColor(itemView.context, R.color.leaf_green))
                } else {
                    tvTargetRemaining.text = getSecsFormatted(target.remainingAmount)
                    tvTargetRemaining.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey))
                }
                (bgProgress.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = target.progressPercent.toFloat() / 100
                bgProgress.requestLayout()
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