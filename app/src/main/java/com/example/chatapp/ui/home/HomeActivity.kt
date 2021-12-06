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
import com.example.chatapp.common.Logger
import com.example.chatapp.databinding.ActivityHomeBinding
import com.example.chatapp.ui.authentication.AuthenticationActivity
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var logger: Logger
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logger = Logger.getInstance(this@HomeActivity)
        setSupportActionBar(binding.toolbar)
        dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.show()
        homeViewModel = ViewModelProvider(this@HomeActivity)[HomeViewModel::class.java]
        homeViewModel.fetchCurrentUserData(this@HomeActivity)

        initClickListeners()
        initObservers()
        initNavigators()
        initViewPager()
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
        val adapter = FragmentAdapter(supportFragmentManager, lifecycle)
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

    private fun initNavigators() {
        homeViewModel.goToLoginScreen.observe(this@HomeActivity) {
            val intent = Intent(this@HomeActivity, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initObservers() {
        homeViewModel.logoutStatus.observe(this@HomeActivity) {
            homeViewModel.goToLoginScreen()
        }

        homeViewModel.getUserFromDbStatus.observe(this@HomeActivity) {
            homeViewModel.currentUser?.let {
                dialog.dismiss()
            }
        }
    }

    private fun initClickListeners() {

    }

    fun logout(){
        homeViewModel.logout(this@HomeActivity)
    }
}