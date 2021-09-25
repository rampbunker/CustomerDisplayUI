package ru.evotor.external.customer_display.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.evotor.external.customer_display.R

class CustomerDisplayAdapter : RecyclerView.Adapter<CustomerDisplayAdapter.CustomerDisplayViewHolder>() {
    private val data = mutableListOf<String>()

    fun appendData(string: String) {
        data.add(string)
        notifyItemInserted(data.size - 1)
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int =
            data.size

    override fun onBindViewHolder(holder: CustomerDisplayViewHolder, position: Int) =
            holder.run {
                setData(data[position])
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerDisplayViewHolder =
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.customer_display_list_item, parent, false)
                    .let { v -> CustomerDisplayViewHolder(v) }


    inner class CustomerDisplayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView by lazy { view.findViewById<TextView>(R.id.textView) }

        fun setData(data: String) {
            textView.text = data
        }
    }
}