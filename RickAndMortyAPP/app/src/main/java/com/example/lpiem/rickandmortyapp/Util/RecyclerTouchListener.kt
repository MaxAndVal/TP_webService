package com.example.lpiem.rickandmortyapp.Util

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerTouchListener : RecyclerView.OnItemTouchListener {
    private var gestureDetector: GestureDetector
    private var clickListener: ClickListener

    constructor(context: Context, recyclerView: RecyclerView, clickListener: ClickListener) {
        this.clickListener = clickListener
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        if (child != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildAdapterPosition(child))
        }
        return false
    }


    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        // nothing implemented
    }


    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // nothing implemented
    }

    interface ClickListener {
        fun onClick(view: View, position: Int)

        fun onLongClick(view: View, position: Int)
    }
}