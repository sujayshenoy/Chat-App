package com.example.chatapp.ui.home.newgroup

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityNewGroupBinding
import com.example.chatapp.ui.home.ViewModelFactory

class NewGroupActivity: AppCompatActivity() {
    private lateinit var binding: ActivityNewGroupBinding
    private lateinit var adapter: NewGroupAdapter
    private lateinit var newGroupViewModel: NewGroupViewModel
    private lateinit var dialog: Dialog
    private lateinit var currentUser: User
    private val logger: Logger = LoggerImpl("NewGroupActivity")
    private val userList = ArrayList<User>()
    private val selectedMembers = ArrayList<User>()

    companion object {
        const val USER_LIST = "userList"
        const val CURRENT_USER = "currentUser"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        newGroupViewModel = ViewModelProvider(this@NewGroupActivity)[NewGroupViewModel::class.java]
        dialog = Dialog(this@NewGroupActivity)
        dialog.setContentView(R.layout.progress_dialog)

        getDataFromIntent()
        initClickListeners()
        initRecyclerView()
        initObservers()
    }

    private fun initObservers() {
        newGroupViewModel.createGroupStatus.observe(this@NewGroupActivity){
            dialog.dismiss()
            finish()
        }
    }

    private fun getDataFromIntent() {
        intent.extras?.let {
            currentUser = it.getSerializable(CURRENT_USER) as User
            val users = it.getSerializable(USER_LIST) as ArrayList<User>
            userList.addAll(users)
        }
    }

    private fun initRecyclerView() {
        adapter = NewGroupAdapter(userList)
        val recycler = binding.memberRecyclerView
        recycler.layoutManager = LinearLayoutManager(this@NewGroupActivity)
        adapter.setOnItemCheckedListener(object : NewGroupAdapter.OnItemChecked{
            override fun onItemChecked(position: Int) {
                selectedMembers.add(userList[position])
                logger.logInfo("selectedMembers: $selectedMembers")
            }

            override fun onItemUnchecked(position: Int) {
                selectedMembers.remove(userList[position])
                logger.logInfo("selectedMembers: $selectedMembers")
            }
        })
        recycler.adapter = adapter
    }

    private fun initClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.createGroupButton.setOnClickListener {
            val groupName = binding.groupName
            if(groupName.text.isEmpty()) {
                groupName.error = getString(R.string.empty_group_name_error_text)
            } else if (selectedMembers.size == 0) {
                groupName.error = getString(R.string.empty_selected_members_error_text)
            } else {
                selectedMembers.add(currentUser)
                newGroupViewModel.createGroup(groupName.text.toString(), selectedMembers)
            }
        }
    }
}