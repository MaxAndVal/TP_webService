package com.example.lpiem.rickandmortyapp.View.Memory

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_memory.*

class MemoryActivity : AppCompatActivity() {

    private var toggled = true
    private var drawable = -1
    private val animationTime = 400L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        first_iv.setOnClickListener {
            val handler = Handler()

            toggled = if (toggled) {
                first_iv.animate().scaleX(0f).setDuration(animationTime).start()
                drawable = R.drawable.card_back
                first_iv.setImageResource(drawable)
                first_iv.isClickable = false
                handler.postDelayed({
                    drawable = R.drawable.rick_icon
                    first_iv.setImageResource(drawable)
                    first_iv.animate().scaleX(1f).setDuration(animationTime).start()
                    first_iv.isClickable = true
                }, animationTime)
                !toggled
            } else {
                first_iv.animate().scaleX(0f).setDuration(animationTime).start()
                drawable = R.drawable.rick_icon
                first_iv.setImageResource(drawable)
                first_iv.isClickable = false
                handler.postDelayed({
                    drawable = R.drawable.card_back
                    first_iv.setImageResource(drawable)
                    first_iv.animate().scaleX(1f).setDuration(animationTime).start()
                    first_iv.isClickable = true
                }, animationTime)
                !toggled
            }
        }
    }
}
