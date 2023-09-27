package com.smartath.taskorganizer.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartath.taskorganizer.adapters.MemberListItemAdapter
import com.smartath.taskorganizer.databinding.DialogMemberListBinding
import com.smartath.taskorganizer.models.User

abstract class MemberListDialog(context: Context,
                                private var members: ArrayList<User>,
                                private val title: String = ""): Dialog(context) {

    private var binding: DialogMemberListBinding? = null
    private var adapter : MemberListItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogMemberListBinding.inflate(layoutInflater)
        setContentView(binding?.root!!)

        setCancelable(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        binding?.titleTv?.text = title

        if(members.size>0){
            binding?.membersRv?.layoutManager = LinearLayoutManager(context)
            binding?.membersRv?.setHasFixedSize(true)

            adapter = MemberListItemAdapter(context, members)

            binding?.membersRv?.adapter = adapter

            adapter!!.setOnClickListener( object : MemberListItemAdapter.OnClickListener{
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }

    }

    protected abstract fun onItemSelected(user: User, action: String)
}