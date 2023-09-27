package com.smartath.taskorganizer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.databinding.ItemBoardLayoutBinding
import com.smartath.taskorganizer.models.Board

class BoardListItemAdapter(private val context: Context,
                           private var boards: ArrayList<Board>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClickListener{
        fun onClick(position: Int, board: Board)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemBoardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val board = boards[position]

        if (holder is ViewHolder){
            Glide
                .with(context)
                .load(board.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board)
                .into(holder.boardImageIv!!)

            holder.boardNameTv?.text = board.name
            holder.createdByTv?.text = "Created by: ${board.createdBy}"
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position, board)
            }
        }
    }

    override fun getItemCount(): Int {
        return boards.size
    }

    inner class ViewHolder(binding: ItemBoardLayoutBinding?): RecyclerView.ViewHolder(binding?.root!!){
        val boardImageIv = binding?.boardImageIv
        val boardNameTv = binding?.boardNameTv
        val createdByTv = binding?.createdByTv
    }
}