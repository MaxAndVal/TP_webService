package com.example.lpiem.rickandmortyapp.View.Memory

import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_memory.*

class MemoryActivity : AppCompatActivity() {

    private val animationTime = 400L
    private var listOfCase: MutableList<Triple<ImageView, Boolean, Int>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        val listOfPictures : MutableList<Int> = mutableListOf(R.drawable.test1, R.drawable.test2, R.drawable.test3,
                R.drawable.test4, R.drawable.test5, R.drawable.test6)

        listOfPictures.addAll(listOfPictures)
        val finalListOfPictures = listOfPictures.shuffled()

        val lisOfImageView = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10,
                iv_11, iv_12)

        for (i in 0..11) {
            listOfCase.add(Triple(lisOfImageView[i], true, finalListOfPictures[i]))
        }

        setAnimationListener(listOfCase)

    }

    private fun setAnimationListener(listOfCase: List<Triple<ImageView, Boolean, Int>>) {
        for (item in listOfCase) {
            val view = item.first
            var toggled = item.second
            val image = item.third
            view.setOnClickListener {
                val handler = Handler()
                var drawable: Int

                toggled = if (toggled) {
                    view.animate().scaleX(0f).setDuration(animationTime).start()
                    drawable = R.drawable.card_back
                    view.setImageResource(drawable)
                    view.isClickable = false
                    handler.postDelayed({
                        drawable = image
                        view.setImageResource(drawable)
                        view.animate().scaleX(1f).setDuration(animationTime).start()
                        view.isClickable = true
                    }, animationTime)
                    !toggled
                } else {
                    view.animate().scaleX(0f).setDuration(animationTime).start()
                    drawable = image
                    view.setImageResource(drawable)
                    view.isClickable = false
                    handler.postDelayed({
                        drawable = R.drawable.card_back
                        view.setImageResource(drawable)
                        view.animate().scaleX(1f).setDuration(animationTime).start()
                        view.isClickable = true
                    }, animationTime)
                    !toggled
                }
            }
        }
    }
}
