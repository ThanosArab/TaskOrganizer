package com.smartath.taskorganizer.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartath.taskorganizer.databinding.ItemColorSelectionBinding

class ColorLabelListItemAdapter(private val context: Context,
                                private var colors: ArrayList<String>,
                                private val selectedColor: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener{
        fun onClick(position: Int, color: String)
    }

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemColorSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val color = colors[position]

        if (holder is ViewHolder){
            holder.mainView?.setBackgroundColor(Color.parseColor(color))

            if (color == selectedColor){
                holder.selectedColorIv?.visibility = View.VISIBLE
            }
            else{
                holder.selectedColorIv?.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onItemClickListener != null){
                    onItemClickListener!!.onClick(position, color)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }


    inner class ViewHolder(binding: ItemColorSelectionBinding?): RecyclerView.ViewHolder(binding?.root!!){
        val mainView = binding?.mainView
        val selectedColorIv = binding?.selectedColorIv
    }
}