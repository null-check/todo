package com.arjun.todo.views

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class ItemSwipeCallback internal constructor(
    private val defaultIconDrawable: Drawable,
    private val defaultBackgroundDrawable: Drawable
) : ItemTouchHelper.Callback() {

    private val mClearPaint: Paint = Paint()
    private val iconWidth: Int = defaultIconDrawable.intrinsicWidth
    private val iconHeight: Int = defaultIconDrawable.intrinsicHeight

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        val direction = if (dX < 0) ItemTouchHelper.LEFT else ItemTouchHelper.RIGHT
        val backgroundDrawable = getBackgroundDrawable(viewHolder, direction)
        if (backgroundDrawable != null) {
            backgroundDrawable.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
            backgroundDrawable.draw(c)
        }

        val drawable = getIconDrawable(viewHolder, direction)
        if (drawable != null) {
            val iconTop = itemView.top + (itemHeight - iconHeight) / 2
            val iconBottom = iconTop + iconHeight
            val iconMargin = (itemHeight - iconHeight) / 2
            val iconLeft: Int
            val iconRight: Int

            if (direction == ItemTouchHelper.LEFT) {
                iconLeft = itemView.right + dX.toInt() + iconMargin
                iconRight = itemView.right + dX.toInt() + iconMargin + iconWidth
            } else {
                iconLeft = itemView.left + iconMargin
                iconRight = itemView.left + iconMargin + iconWidth
            }

            drawable.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            drawable.draw(c)
        }
    }

    open fun getBackgroundDrawable(viewHolder: RecyclerView.ViewHolder, direction: Int): Drawable? {
        return defaultBackgroundDrawable
    }

    open fun getIconDrawable(viewHolder: RecyclerView.ViewHolder, direction: Int): Drawable? {
        return defaultIconDrawable
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.5f
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}