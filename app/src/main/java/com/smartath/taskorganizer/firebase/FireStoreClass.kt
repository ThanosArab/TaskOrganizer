package com.smartath.taskorganizer.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.smartath.taskorganizer.activities.*
import com.smartath.taskorganizer.models.Board
import com.smartath.taskorganizer.models.User
import com.smartath.taskorganizer.utils.Constants

class FireStoreClass {

    private val fireStore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String{
        val user = FirebaseAuth.getInstance().currentUser
        var userId = ""

        if (user != null){
            userId = user.uid
        }
        return userId
    }

    fun registerUser(activity: SignUpActivity, user: User){
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccessfully(user)
            }.addOnFailureListener {
                    e ->
                activity.cancelProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun loadUserData(activity: Activity, showBoardsList: Boolean = false){
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity -> {
                        activity.signInSuccessfully(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateUserDetails(loggedInUser, showBoardsList)
                    }
                    is ProfileActivity -> {
                        activity.setUpUserUiData(loggedInUser)
                    }
                }
            }.addOnFailureListener {
                    e ->
                when(activity){
                    is SignInActivity -> {
                        activity.cancelProgressDialog()
                    }
                    is MainActivity -> {
                        activity.cancelProgressDialog()
                    }
                    is ProfileActivity -> {
                        activity.cancelProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }

    }

    fun updateUserData (activity: Activity, userHashMap: HashMap<String, Any>){
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){
                    is ProfileActivity -> {
                        activity.profileUpdatedSuccessfully()
                    }
                }
            }.addOnFailureListener {
                e ->
                when(activity){
                    is ProfileActivity -> {
                        activity.cancelProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        fireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                    e ->
                activity.cancelProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun getBoardsList(activity: MainActivity){
        fireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->

                val boards: ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boards.add(board)
                }
                activity.setUpRecyclerView(boards)
            }.addOnFailureListener {
                    e ->
                activity.cancelProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String){
        fireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                document ->

                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)
            }.addOnFailureListener {
                    e ->
                activity.cancelProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        fireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                when(activity){
                    is TaskListActivity -> {
                        activity.addUpdateTaskListSuccessfully()
                    }
                    is CardDetailsActivity -> {
                        activity.addUpdateListSuccessfully()
                    }
                }
            }.addOnFailureListener {
                    e ->
                when(activity){
                    is TaskListActivity -> {
                        activity.cancelProgressDialog()
                    }
                    is CardDetailsActivity -> {
                        activity.cancelProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun getAssignedMembersListDetails(activity: Activity, assignedTo: ArrayList<String>){
        fireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->

                val users: ArrayList<User> = ArrayList()
                for (i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    users.add(user)
                }
                when(activity){
                    is TaskListActivity -> {
                        activity.setUpTaskListViewMembers(users)
                    }
                    is MembersActivity -> {
                        activity.setUpMembersList(users)
                    }
                }
            }.addOnFailureListener {
                    e ->
                when(activity){
                    is TaskListActivity -> {
                        activity.cancelProgressDialog()
                    }
                    is MembersActivity -> {
                        activity.cancelProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){
        fireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                    document ->
                if (document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }
                else{
                    activity.cancelProgressDialog()
                    activity.showSnackBarError("No such Member found.")
                }
            }.addOnFailureListener {
                    e ->
                activity.cancelProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        fireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignedSuccessfully(user)
            }.addOnFailureListener {
                    e ->
                activity.cancelProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }

    fun deleteBoard(activity:TaskListActivity, boardId:String){
        fireStore.collection(Constants.BOARDS)
            .document(boardId)
            .delete()
            .addOnSuccessListener {
                Log.i("delete","Board deleted successfully")
                Toast.makeText(activity,"Board deleted successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                    e ->
                activity.cancelProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: $e")
            }
    }


}