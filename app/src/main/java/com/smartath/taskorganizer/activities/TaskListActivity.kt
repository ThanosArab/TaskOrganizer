package com.smartath.taskorganizer.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.adapters.TaskListItemAdapter
import com.smartath.taskorganizer.databinding.ActivityTaskListBinding
import com.smartath.taskorganizer.firebase.FireStoreClass
import com.smartath.taskorganizer.models.Board
import com.smartath.taskorganizer.models.Card
import com.smartath.taskorganizer.models.Task
import com.smartath.taskorganizer.models.User
import com.smartath.taskorganizer.utils.Constants

class TaskListActivity : BaseActivity() {

    private var binding: ActivityTaskListBinding? = null

    private lateinit var boardDetails: Board
    private lateinit var boardDocumentId: String

    lateinit var assignedMemberDetailList: ArrayList<User>

    private val startRefreshActivity: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == RESULT_OK){
            showProgressDialog()
            FireStoreClass().getBoardDetails(this, boardDocumentId)
        }
        else{
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog()
        FireStoreClass().getBoardDetails(this, boardDocumentId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionMembers -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, boardDetails)
                startRefreshActivity.launch(intent)
            }
            R.id.actionDeleteBoard -> {
                deleteBoardDialog(boardDetails.name)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun boardDetails(board: Board){
        boardDetails = board

        setUpToolbar(binding?.taskListToolbar!!, boardDetails.name, R.drawable.ic_back_white)

        FireStoreClass().getAssignedMembersListDetails(this, boardDetails.assignedTo)
    }

    fun addUpdateTaskListSuccessfully(){
        FireStoreClass().getBoardDetails(this, boardDocumentId)
    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName, FireStoreClass().getCurrentUserId())

        boardDetails.taskList.add(0, task)
        boardDetails.taskList.removeAt(boardDetails.taskList.size -1)

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, boardDetails)
    }

    fun updateTaskList(position: Int, taskListName: String, task: Task){
        val taskModel = Task(taskListName, task.createdBy)

        boardDetails.taskList[position] = taskModel
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, boardDetails)
    }

    fun deleteTaskList(position: Int){
        boardDetails.taskList.removeAt(position)
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, boardDetails)
    }

    fun setUpTaskListViewMembers(users: ArrayList<User>){
        assignedMemberDetailList = users

        cancelProgressDialog()

        val task = Task("Add Task")
        boardDetails.taskList.add(task)

        binding?.taskListRv?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.taskListRv?.setHasFixedSize(true)

        val adapter = TaskListItemAdapter(this, boardDetails.taskList)
        binding?.taskListRv?.adapter = adapter
    }

    fun addCardToTaskList(position: Int, cardName: String){
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        val cardsAssignedUsersList: ArrayList<String> = ArrayList()
        cardsAssignedUsersList.add(FireStoreClass().getCurrentUserId())

        val card = Card(cardName, FireStoreClass().getCurrentUserId(), cardsAssignedUsersList)

        val cards = boardDetails.taskList[position].cards
        cards.add(card)

        val task = Task(boardDetails.taskList[position].title,
                        boardDetails.taskList[position].createdBy, cards)

        boardDetails.taskList[position] = task

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, boardDetails)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int){
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, boardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, assignedMemberDetailList)
        startRefreshActivity.launch(intent)
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>){
        boardDetails.taskList.removeAt(boardDetails.taskList.size -1)

        boardDetails.taskList[taskListPosition].cards = cards

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, boardDetails)
    }

    private fun deleteBoardDialog(boardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(boardName)
        builder.setMessage("Are you sure you want to delete $boardName?")
        builder.setIcon(android.R.drawable.ic_delete)
        builder.setPositiveButton("Yes"){
                dialogInterface, _ ->
            dialogInterface.dismiss()
            showProgressDialog()
            FireStoreClass().deleteBoard(this, boardDocumentId)
            setResult(RESULT_OK)
            finish()
        }
        builder.setNegativeButton("No"){
                dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}