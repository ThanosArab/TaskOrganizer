package com.smartath.taskorganizer.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartath.taskorganizer.activities.TaskListActivity
import com.smartath.taskorganizer.databinding.ItemCardLayoutBinding
import com.smartath.taskorganizer.models.Card
import com.smartath.taskorganizer.models.SelectedMembers

class CardListItemAdapter(private val context: Context,
                          private var cards: ArrayList<Card>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClickListener{
        fun onClick(position: Int)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = cards[position]

        if (holder is ViewHolder){
            if(card.labelColor.isNotEmpty()){
                holder.labelColorView?.visibility = View.VISIBLE
                holder.labelColorView?.setBackgroundColor(Color.parseColor(card.labelColor))
            }
            else{
                holder.labelColorView?.visibility = View.GONE
            }

            holder.cardNameTv?.text = card.name

            if((context as TaskListActivity).assignedMemberDetailList.size > 0){
                val selectedMembers: ArrayList<SelectedMembers> = ArrayList()

                for(i in context.assignedMemberDetailList.indices){
                    for(j in card.assignedTo){
                        if (context.assignedMemberDetailList[i].id == j){
                            val selectedMember = SelectedMembers(context.assignedMemberDetailList[i].id, context.assignedMemberDetailList[i].image)
                            selectedMembers.add(selectedMember)
                        }
                    }
                }

                if (selectedMembers.size > 0){
                    if (selectedMembers.size == 1 && selectedMembers[0].id == card.createdBy){
                        holder.cardSelectedMembersRv?.visibility = View.GONE
                    }
                    else{
                        holder.cardSelectedMembersRv?.visibility = View.VISIBLE

                        holder.cardSelectedMembersRv?.layoutManager = GridLayoutManager(context, 4)

                        val adapter = CardMembersListItemAdapter(context, selectedMembers, false)
                        holder.cardSelectedMembersRv?.adapter = adapter

                        adapter.setOnClickListener(object : CardMembersListItemAdapter.OnClickListener{
                            override fun onClick() {
                                if (onClickListener != null){
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                }
                else{
                    holder.cardSelectedMembersRv?.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    inner class ViewHolder(binding: ItemCardLayoutBinding?): RecyclerView.ViewHolder(binding?.root!!){
        val labelColorView = binding?.labelColorView
        val cardNameTv = binding?.cardNameTv
        val cardSelectedMembersRv = binding?.cardSelectedMembersRv

    }
}