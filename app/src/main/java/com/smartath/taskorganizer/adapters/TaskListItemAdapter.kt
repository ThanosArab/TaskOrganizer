package com.smartath.taskorganizer.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartath.taskorganizer.activities.TaskListActivity
import com.smartath.taskorganizer.databinding.ItemTaskLayoutBinding
import com.smartath.taskorganizer.models.Task
import java.util.*
import kotlin.collections.ArrayList

class TaskListItemAdapter(private val context: Context,
                          private var tasks: ArrayList<Task>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var positionDraggedFrom = -1
    private var positionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ItemTaskLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams = LinearLayout.LayoutParams((parent.width*0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(15.toDp().toPx(), 0, 40.toDp().toPx(), 0)
        view.root.layoutParams = layoutParams
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = tasks[position]

        if (holder is ViewHolder){
            if (position == tasks.size-1){
                holder.addTaskListTv?.visibility = View.VISIBLE
                holder.taskItemLayout?.visibility = View.GONE
            }
            else{
                holder.addTaskListTv?.visibility = View.GONE
                holder.taskItemLayout?.visibility = View.VISIBLE
            }

            holder.taskListTitleTv?.text = task.title

            holder.addTaskListTv?.setOnClickListener {
                holder.addTaskListTv.visibility = View.GONE
                holder.addTaskListNameCv?.visibility = View.VISIBLE
            }

            holder.cancelTaskListNameBtn?.setOnClickListener {
                holder.addTaskListTv?.visibility = View.VISIBLE
                holder.addTaskListNameCv?.visibility = View.GONE
            }

            holder.doneTaskListNameBtn?.setOnClickListener {
                val listName = holder.taskListNameEt?.text.toString()

                if (listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }
                else{
                    Toast.makeText(context, "Please enter a List Name.", Toast.LENGTH_LONG).show()
                }
            }

            holder.editTaskListTitleBtn?.setOnClickListener {
                holder.taskListNameEt?.setText(task.title)
                holder.titleViewLayout?.visibility = View.GONE
                holder.taskListNameCv?.visibility = View.VISIBLE
            }

            holder.cancelTaskTitleBtn?.setOnClickListener {
                holder.titleViewLayout?.visibility = View.VISIBLE
                holder.taskListNameCv?.visibility = View.GONE
            }

            holder.doneTaskTitleBtn?.setOnClickListener {
                val listName = holder.taskTitleEt?.text.toString()

                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.updateTaskList(position, listName, task)
                    }
                }
                else{
                    Toast.makeText(context, "Please enter a List Name.", Toast.LENGTH_LONG).show()
                }
            }

            holder.deleteTaskListTitleBtn?.setOnClickListener {
                deleteListDialog(position, task.title)
            }

            holder.addCardTv?.setOnClickListener {
                holder.addCardTv.visibility = View.GONE
                holder.addCardCv?.visibility = View.VISIBLE
            }

            holder.cancelCardNameBtn?.setOnClickListener {
                holder.addCardTv?.visibility = View.VISIBLE
                holder.addCardCv?.visibility = View.GONE
            }

            holder.doneCardNameBtn?.setOnClickListener {
                val cardName = holder.cardNameEt?.text.toString()

                if (cardName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.addCardToTaskList(position, cardName)
                    }
                }
                else{
                    Toast.makeText(context, "Please enter a Card Name.", Toast.LENGTH_LONG).show()
                }
            }

            holder.cardListRv?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.cardListRv?.setHasFixedSize(true)

            val adapter = CardListItemAdapter(context, task.cards)
            holder.cardListRv?.adapter = adapter

            adapter.setOnClickListener(object : CardListItemAdapter.OnClickListener{
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity){
                        context.cardDetails(position, cardPosition = cardPosition)
                    }
                }
            })

            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            holder.cardListRv?.addItemDecoration(dividerItemDecoration)

            val helper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                    val startPosition = viewHolder.adapterPosition
                    val endPosition = target.adapterPosition

                    if (positionDraggedFrom == -1){
                        positionDraggedFrom = startPosition
                    }
                    positionDraggedTo = endPosition

                    Collections.swap(tasks[position].cards, startPosition, endPosition)
                    adapter.notifyItemMoved(startPosition, endPosition)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    if (positionDraggedFrom != -1 && positionDraggedTo != -1 && positionDraggedFrom != positionDraggedTo){
                        (context as TaskListActivity).updateCardsInTaskList(position, tasks[position].cards)
                    }
                    positionDraggedFrom = -1
                    positionDraggedTo = -1
                }
            })

            helper.attachToRecyclerView(holder.cardListRv)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    inner class ViewHolder(binding: ItemTaskLayoutBinding?): RecyclerView.ViewHolder(binding?.root!!){
        val addTaskListTv = binding?.addTaskListTv
        val addTaskListNameCv = binding?.addTaskListNameCv
        val cancelTaskListNameBtn = binding?.cancelTaskListNameBtn
        val taskListNameEt = binding?.taskListNameEt
        val doneTaskListNameBtn = binding?.doneTaskListNameBtn
        val taskItemLayout = binding?.taskItemLayout
        val titleViewLayout = binding?.titleViewLayout
        val taskListTitleTv = binding?.taskListTitleTv
        val editTaskListTitleBtn = binding?.editTaskListTitleBtn
        val deleteTaskListTitleBtn = binding?.deleteTaskListTitleBtn
        val taskListNameCv = binding?.taskListNameCv
        val cancelTaskTitleBtn = binding?.cancelTaskTitleBtn
        val taskTitleEt = binding?.taskTitleEt
        val doneTaskTitleBtn = binding?.doneTaskTitleBtn
        val cardListRv = binding?.cardListRv
        val addCardCv = binding?.addCardCv
        val cancelCardNameBtn = binding?.cancelCardNameBtn
        val cardNameEt = binding?.cardNameEt
        val doneCardNameBtn = binding?.doneCardNameBtn
        val addCardTv = binding?.addCardTv
    }

    private fun deleteListDialog(position: Int, title: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setIcon(android.R.drawable.ic_delete)
        builder.setPositiveButton("Yes"){
                dialogInterface, _ ->
            dialogInterface.dismiss()
            if (context is TaskListActivity){
                context.deleteTaskList(position)
            }
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