package com.example.chatapp.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil.Companion.USER_ID
import com.example.chatapp.common.sharedpreferences.SharedPrefUtilImpl
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityHomeBinding
import com.example.chatapp.ui.authentication.AuthenticationActivity
import com.example.chatapp.ui.home.chat.ChatFragment
import com.example.chatapp.ui.peerchat.PeerChatActivity
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var logger: Logger
    private lateinit var dialog: Dialog
    private lateinit var sharedPrefUtil: SharedPrefUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefUtil = SharedPrefUtilImpl.getInstance(this@HomeActivity)

        logger = LoggerImpl("Home Activity")
        setSupportActionBar(binding.toolbar)
        dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.show()

        initViewModel()
        initObservers()
    }

    private fun initViewModel() {
        val uid = sharedPrefUtil.getString(USER_ID)
        uid?.let {
            homeViewModel = ViewModelProvider(this@HomeActivity, ViewModelFactory(HomeViewModel(it)))[HomeViewModel::class.java]
        } ?: logger.logError("Null user id after auth")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logoutButton -> logout()
        }
        return true
    }

    private fun initViewPager() {
        val adapter = FragmentAdapter(supportFragmentManager, lifecycle, object : ChatFragment.ChatFragmentHostListener{
            override fun onChatItemClicked(selectedUser: User) {
                val intent = Intent(this@HomeActivity, PeerChatActivity::class.java)
                intent.putExtra(PeerChatActivity.ARG_USER_RECEIVER, selectedUser)
                intent.putExtra(PeerChatActivity.ARG_USER_SENDER, homeViewModel.currentUser)
                startActivity(intent)
            }
        })
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        viewPager.adapter = adapter
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.chats_tab_text)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.group_chats_tab_text)))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { viewPager.currentItem = it.position }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    private fun goToLoginScreen() {
        val intent = Intent(this@HomeActivity, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initObservers() {
        homeViewModel.logoutStatus.observe(this@HomeActivity) {
            sharedPrefUtil.clearAll()
            goToLoginScreen()
        }

        homeViewModel.getUserFromDbStatus.observe(this@HomeActivity) {
            homeViewModel.currentUser?.let {
                initViewPager()
                dialog.dismiss()
            }
        }
    }

    private fun logout(){
        homeViewModel.logout()
    }
}