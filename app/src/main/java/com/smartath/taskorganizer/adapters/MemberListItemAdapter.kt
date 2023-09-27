package com.smartath.taskorganizer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.databinding.ItemMemberLayoutBinding
import com.smartath.taskorganizer.models.User
import com.smartath.taskorganizer.utils.Constants

class MemberListItemAdapter(private val context: Context,
                            private var members: ArrayList<User>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClickListener{
        fun onClick(position: Int, user: User, action: String)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemMemberLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member = members[position]

        if (holder is ViewHolder){

            Glide
                .with(context)
                .load(member.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(holder.memberImageIv!!)

            holder.memberNameTv?.text = member.name
            holder.memberEmailTv?.text = member.email

            if(member.selected){
                holder.selectedMemberIv?.visibility = View.VISIBLE
            }
            else{
                holder.selectedMemberIv?.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    if (member.selected) {
                        onClickListener!!.onClick(position, member, Constants.UN_SELECT)
                    }
                    else {
                        onClickListener!!.onClick(position, member, Constants.SELECT)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    inner class ViewHolder(binding: ItemMemberLayoutBinding?): RecyclerView.ViewHolder(binding?.root!!){
        val memberImageIv = binding?.memberImageIv
        val memberNameTv = binding?.memberNameTv
        val memberEmailTv = binding?.memberEmailTv
        val selectedMemberIv = binding?.selectedMemberIv
    }
}