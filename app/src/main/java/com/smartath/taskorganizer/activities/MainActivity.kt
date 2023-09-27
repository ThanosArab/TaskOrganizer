package com.smartath.taskorganizer.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.adapters.BoardListItemAdapter
import com.smartath.taskorganizer.databinding.ActivityMainBinding
import com.smartath.taskorganizer.databinding.NavigationHeaderBinding
import com.smartath.taskorganizer.firebase.FireStoreClass
import com.smartath.taskorganizer.models.Board
import com.smartath.taskorganizer.models.User
import com.smartath.taskorganizer.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null

    private lateinit var userName: String

    private val startUpdateActivity: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == RESULT_OK){
            FireStoreClass().loadUserData(this)
        }
        else{
            Log.e("onActivityResult()", "Profile update cancelled by user")
        }
    }

    private val startUpdateBoard: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == RESULT_OK){
            FireStoreClass().getBoardsList(this)
        }
        else{
            Log.e("onActivityResult()", "Boards update cancelled by user")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpToolbarMain()

        binding?.navigationView?.setNavigationItemSelectedListener(this)

        showProgressDialog()
        FireStoreClass().loadUserData(this, true)

        binding?.appBarMain?.addFab?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, userName)
            startUpdateBoard.launch(intent)
        }

        onBackPressedCall()
    }

    private fun setUpToolbarMain(){
        setSupportActionBar(binding?.appBarMain?.mainToolbar)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = resources.getString(R.string.app_name)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

            binding?.appBarMain?.mainToolbar?.setNavigationOnClickListener {
                if(binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
                    binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                }
                else{
                    binding?.drawerLayout?.openDrawer(GravityCompat.START)
                }
            }
        }
    }

    fun setUpRecyclerView(boards: ArrayList<Board>){
        cancelProgressDialog()

        val boardsRv = binding?.appBarMain?.contentMain?.boardsRv
        val emptyTv = binding?.appBarMain?.contentMain?.emptyBoardsTv

        if (boards.size > 0){
            boardsRv?.visibility = View.VISIBLE
            emptyTv?.visibility = View.GONE

            boardsRv?.layoutManager = LinearLayoutManager(this)
            boardsRv?.setHasFixedSize(true)

            val adapter = BoardListItemAdapter(this, boards)
            boardsRv?.adapter = adapter

            adapter.setOnClickListener(object : BoardListItemAdapter.OnClickListener{
                override fun onClick(position: Int, board: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, board.documentId)
                    startUpdateBoard.launch(intent)
                }
            })
        }
        else{
            boardsRv?.visibility = View.GONE
            emptyTv?.visibility = View.VISIBLE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navProfile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startUpdateActivity.launch(intent)

            }
            R.id.navSignOut -> {
                FirebaseAuth.getInstance().signOut()

                val intent  = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateUserDetails(user: User, showBoardsList: Boolean){

        userName = user.name

        val headerView = binding?.navigationView?.getHeaderView(0)
        val headerBinding = NavigationHeaderBinding.bind(headerView!!)

        imageWithGlide(this, headerBinding.userImageIv, user.image.toUri())

        headerBinding.userNameTv.text = user.name

        if (showBoardsList){
            FireStoreClass().getBoardsList(this)
        }
    }

    private fun onBackPressedCall(){
        onBackPressedDispatcher.addCallback(this, object  : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
                    binding?.drawerLayout?.closeDrawer(GravityCompat.START)
                }
                else{
                    this.isEnabled = false
                    doubleBackToExit()
                    this.isEnabled = true
                }
                finish()
            }
        })
    }


}