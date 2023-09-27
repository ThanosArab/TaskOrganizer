package com.smartath.taskorganizer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.databinding.ItemCardMemberSelectionBinding
import com.smartath.taskorganizer.models.SelectedMembers

class CardMembersListItemAdapter(private val context: Context,
                                 private var members: ArrayList<SelectedMembers>,
                                 private val assignMembers: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClickListener{
        fun onClick()
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemCardMemberSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member = members[position]

        if(holder is ViewHolder){
            if (position == members.size-1 && assignMembers){
                holder.addMemberIv?.visibility = View.VISIBLE
                holder.selectedMemberIv?.visibility = View.GONE
            }
            else{
                holder.addMemberIv?.visibility = View.GONE
                holder.selectedMemberIv?.visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(member.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user)
                    .into(holder.selectedMemberIv!!)
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    inner class ViewHolder(binding: ItemCardMemberSelectionBinding?): RecyclerView.ViewHolder(binding?.root!!){
        val selectedMemberIv = binding?.selectedMemberIv
        val addMemberIv = binding?.addMemberIv
    }
}