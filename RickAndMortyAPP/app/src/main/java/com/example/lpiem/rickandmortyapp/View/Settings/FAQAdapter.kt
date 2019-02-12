package com.example.lpiem.rickandmortyapp.View.Settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Model.FAQ
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.faq_item.view.*

class FAQAdapter(private val dataSet: List<FAQ>, private val listener: SettingsOnClickInterface) : RecyclerView.Adapter<FAQAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.faq_item, parent, false)
        return FAQAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.faqQuestion.text = item.question
        holder.faqResponse.text = item.response
        holder.faqQuestion.setOnClickListener { listener.todo(holder) }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val faqQuestion = view.tv_question
        val faqResponse = view.tv_response
    }

}
