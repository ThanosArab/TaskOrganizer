package com.smartath.taskorganizer.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.adapters.CardMembersListItemAdapter
import com.smartath.taskorganizer.databinding.ActivityCardDetailsBinding
import com.smartath.taskorganizer.dialogs.ColorLabelListDialog
import com.smartath.taskorganizer.dialogs.MemberListDialog
import com.smartath.taskorganizer.firebase.FireStoreClass
import com.smartath.taskorganizer.models.Board
import com.smartath.taskorganizer.models.Card
import com.smartath.taskorganizer.models.SelectedMembers
import com.smartath.taskorganizer.models.User
import com.smartath.taskorganizer.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private var binding: ActivityCardDetailsBinding? = null

    private lateinit var boardDetails: Board

    private var taskListPosition = -1
    private var cardListPosition = -1

    private lateinit var membersDetailList: ArrayList<User>

    private var selectedColor: String = ""

    private var selectedDueDateInMills: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentData()
        setUpToolbar(binding?.cardDetailsToolbar!!, boardDetails.taskList[taskListPosition].cards[cardListPosition].name, R.drawable.ic_back_white)

        binding?.cardDetailsNameEt?.setText(boardDetails.taskList[taskListPosition].cards[cardListPosition].name)
        binding?.cardDetailsNameEt?.setSelection(binding?.cardDetailsNameEt?.text.toString().length)

        selectedColor = boardDetails.taskList[taskListPosition].cards[cardListPosition].labelColor
        if (selectedColor.isNotEmpty()){
            setColor()
        }

        binding?.cardDetailsUpdateBtn?.setOnClickListener {
            if (binding?.cardDetailsNameEt?.text.toString().isNotEmpty()){
                updateCardDetails()
            }
            else{
                Toast.makeText(this, "Please enter a Card Name.", Toast.LENGTH_LONG).show()
            }
        }

        binding?.selectColorTv?.setOnClickListener {
            colorLabelListDialog()
        }
        binding?.selectMembersTv?.setOnClickListener {
            membersListDialog()
        }

        selectedDueDateInMills = boardDetails.taskList[taskListPosition].cards[cardListPosition].dueDate
        if (selectedDueDateInMills > 0){
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = sdf.format(Date(selectedDueDateInMills))
            binding?.selectDateTv?.text = selectedDate
        }

        binding?.selectDateTv?.setOnClickListener {
            showDatePicker()
        }

        setUpSelectedMembersList()

        onBackPressedCall()
    }

    @Suppress("Deprecation")
    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            boardDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                intent.getParcelableExtra(Constants.BOARD_DETAIL, Board::class.java)!!
            } else{
                intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            }
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            taskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            cardListPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            membersDetailList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST, User::class.java)!!
            } else{
                intent.getParcelableArrayListExtra<User>(Constants.BOARD_MEMBERS_LIST)!!
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionDeleteCard -> {
                deleteCardDialog(boardDetails.taskList[taskListPosition].cards[cardListPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateListSuccessfully(){
        cancelProgressDialog()
        setResult(RESULT_OK)
        finish()
    }

    private fun updateCardDetails(){
        val card = Card (
            binding?.cardDetailsNameEt?.text.toString(),
            boardDetails.taskList[taskListPosition].cards[cardListPosition].createdBy,
            boardDetails.taskList[taskListPosition].cards[cardListPosition].assignedTo,
            selectedColor, selectedDueDateInMills)

        val taskList = boardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        boardDetails.taskList[taskListPosition].cards[cardListPosition] = card

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, boardDetails)
    }

    private fun deleteCardDialog(cardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(cardName)
        builder.setMessage("Are you sere you want to delete $cardName?")
        builder.setIcon(android.R.drawable.ic_delete)
        builder.setPositiveButton("Yes"){ dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton("No"){ dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteCard(){
        val cardList = boardDetails.taskList[taskListPosition].cards
        cardList.removeAt(cardListPosition)

        val taskList = boardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[taskListPosition].cards = cardList

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, boardDetails)
    }

    private fun colorsList(): ArrayList<String>{
        val colors: ArrayList<String> = ArrayList()
        colors.add("#43C86F")
        colors.add("#0C90F1")
        colors.add("#F72400")
        colors.add("#7A8089")
        colors.add("#D57C1D")
        colors.add("#770000")
        colors.add("#0022F8")
        return colors
    }

    private fun setColor(){
        binding?.selectColorTv?.text = ""
        binding?.selectColorTv?.setBackgroundColor(Color.parseColor(selectedColor))
    }

    private fun colorLabelListDialog(){
        val colors = colorsList()

        val colorListDialog = object : ColorLabelListDialog(this, colors, "Select Label color", selectedColor){
            override fun onItemSelected(color: String) {
                selectedColor = color
                setColor()
            }
        }
        colorListDialog.show()
    }

    private fun membersListDialog(){
        val assignedMembersList = boardDetails.taskList[taskListPosition].cards[cardListPosition].assignedTo

        if(assignedMembersList.size > 0){
            for (i in membersDetailList.indices){
                for (j in assignedMembersList){
                    if (membersDetailList[i].id == j){
                        membersDetailList[i].selected = true
                    }
                }
            }
        }
        else{
            for (i in membersDetailList.indices){
                membersDetailList[i].selected = false
            }
        }

        val memberListDialog = object : MemberListDialog(this ,membersDetailList, "Select Member"){
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT){
                    if(!boardDetails.taskList[taskListPosition].cards[cardListPosition].assignedTo.contains(user.id)){
                        boardDetails.taskList[taskListPosition].cards[cardListPosition].assignedTo.add(user.id)
                    }
                }
                else{
                    boardDetails.taskList[taskListPosition].cards[cardListPosition].assignedTo.remove(user.id)
                    for (i in membersDetailList.indices){
                        if (membersDetailList[i].id == user.id){
                            membersDetailList[i].selected = false
                        }
                    }

                }
                setUpSelectedMembersList()
            }
        }
        memberListDialog.show()
    }

    private fun setUpSelectedMembersList(){
        val assignedMembersList = boardDetails.taskList[taskListPosition].cards[cardListPosition].assignedTo

        val selectedMembers: ArrayList<SelectedMembers> = ArrayList()

        for(i in membersDetailList.indices){
            for(j in assignedMembersList){
                if (membersDetailList[i].id == j){
                    val selectedMember = SelectedMembers(membersDetailList[i].id, membersDetailList[i].image)
                    selectedMembers.add(selectedMember)
                }
            }
        }

        if (selectedMembers.size > 0){
            selectedMembers.add(SelectedMembers("", ""))
            binding?.selectMembersTv?.visibility = View.GONE
            binding?.selectedMembersRv?.visibility = View.VISIBLE

            binding?.selectedMembersRv?.layoutManager = GridLayoutManager(this, 6)
            val adapter = CardMembersListItemAdapter(this, selectedMembers, true)

            binding?.selectedMembersRv?.adapter = adapter

            adapter.setOnClickListener(object : CardMembersListItemAdapter.OnClickListener{
                override fun onClick() {
                    membersListDialog()
                }
            })

        }
        else{
            binding?.selectMembersTv?.visibility = View.VISIBLE
            binding?.selectedMembersRv?.visibility = View.GONE
        }
    }

    private fun showDatePicker(){
        val calendar = Calendar.getInstance()
        val yearCal = calendar.get(Calendar.YEAR)
        val monthCal = calendar.get(Calendar.MONTH)
        val dayCal = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                _, year, month, day ->
            val dayOfMonth = if (day<10) "0$day" else "$day"
            val monthOfYear = if ((month+1)<10) "0${month+1}" else "${month+1}"

            val selectedDate = "$dayOfMonth/$monthOfYear/$year"
            binding?.selectDateTv?.text = selectedDate

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val theDate = sdf.parse(selectedDate)

            selectedDueDateInMills = theDate!!.time
        }, yearCal, monthCal, dayCal).show()
    }


    private fun onBackPressedCall() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                setResult(RESULT_OK)
                finish()
            }
        })
    }
}