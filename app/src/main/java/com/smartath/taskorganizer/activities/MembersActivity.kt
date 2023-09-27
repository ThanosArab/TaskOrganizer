package com.smartath.taskorganizer.activities

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.adapters.MemberListItemAdapter
import com.smartath.taskorganizer.databinding.ActivityMembersBinding
import com.smartath.taskorganizer.databinding.DialogSearchMemberBinding
import com.smartath.taskorganizer.firebase.FireStoreClass
import com.smartath.taskorganizer.models.Board
import com.smartath.taskorganizer.models.User
import com.smartath.taskorganizer.utils.Constants

class MembersActivity : BaseActivity() {

    private var binding: ActivityMembersBinding? = null

    private lateinit var boardDetails: Board

    private lateinit var assignedMembersList: ArrayList<User>

    private var anyChangesMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            boardDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Constants.BOARD_DETAIL, Board::class.java)!!
            } else {
                intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            }
        }

        setUpToolbar(binding?.membersToolbar!!, boardDetails.name, R.drawable.ic_back_white)

        onBackPressedCall()

        showProgressDialog()
        FireStoreClass().getAssignedMembersListDetails(this, boardDetails.assignedTo)
    }

    fun setUpMembersList(members: ArrayList<User>){
        assignedMembersList = members

        cancelProgressDialog()

        binding?.membersRv?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.membersRv?.setHasFixedSize(true)

        val adapter = MemberListItemAdapter(this, members)
        binding?.membersRv?.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionAddMember -> {
                showAddMemberDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAddMemberDialog(){
        val dialog = Dialog(this)
        val binding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.addTv.setOnClickListener {
            val email = binding.emailEt.text.toString()
            if (email.isNotEmpty()){
                dialog.dismiss()
                FireStoreClass().getMemberDetails(this@MembersActivity, email)
            }
            else{
                Toast.makeText(this, "Please enter an email address.", Toast.LENGTH_LONG).show()
            }
        }
        binding.cancelTv.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberDetails(user: User){
        boardDetails.assignedTo.add(user.id)

        FireStoreClass().assignMemberToBoard(this, boardDetails, user)
    }

    fun memberAssignedSuccessfully(user: User){
        cancelProgressDialog()
        assignedMembersList.add(user)

        anyChangesMade = true

        setUpMembersList(assignedMembersList)
    }

    private fun onBackPressedCall() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (anyChangesMade){
                    setResult(RESULT_OK)
                }
                finish()
            }
        })
    }
}