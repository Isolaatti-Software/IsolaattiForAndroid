/*
* Isolaatti Software, Erik Cavazos, 2022
* Isolaatti Project: Profile
*
* This is the profile activity. It has an hybrid interface; it's partially built using JetPack Compose
* The main pager is using Android Views, but the content of every fragment is using JetPack Compose View
*/

package com.erik.isolaatti.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.palette.graphics.Palette
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

import com.erik.isolaatti.R
import com.erik.isolaatti.activities.ui.myProfile.*
import com.erik.isolaatti.classes.SessionTokenValidated
import com.erik.isolaatti.services.TokenStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


/*
* This is the adapter for the tabs. Define here what screens to render. It's recommended to
* pass parameters to the constructor to know what to do.
* For example, you might want to render and extra tab for my profile so that the user can edit their profile
*/
class ProfileTabsAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ProfileInfoProfile()
            1 -> ProfilePostsFragment()
            2 -> ProfileFollowersFragment()
            3 -> ProfileFollowingFragment()
            else -> {ProfilePostsFragment()}
        }
    }

}

class MyProfile : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState!=null) {
            val sessionTokenValidated = SessionTokenValidated(
                savedInstanceState.getBoolean("isValid"),
                savedInstanceState.getInt("userId")
            )
            TokenStorage(application).setSessionTokenValidated(sessionTokenValidated)
        }
        setContentView(R.layout.my_profile_activity)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        intent.extras?.let {
            viewModel.profileId.value = it.getInt("userId",-1)
        }

        // show error messages that are emitted from the view model
        lifecycleScope.launch {
            viewModel.noInternetConnectionError.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collect {
                if(it){
                    Snackbar.make(findViewById(R.id.snackbar_view),
                        R.string.error_no_internet,BaseTransientBottomBar.LENGTH_INDEFINITE).show()
                } else {

                }
            }
        }

        lifecycleScope.launch {
            viewModel.connectionTimedOut.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collect {
                if(it){
                    Snackbar.make(findViewById(R.id.snackbar_view),
                        R.string.error_timeout,BaseTransientBottomBar.LENGTH_INDEFINITE).show()
                } else {

                }
            }
        }

        lifecycleScope.launch {
            viewModel.signInAgain.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collect {
                Log.println(Log.ERROR,"Error de autenticacion", "Ha ocurrido un error de autenticacion, vuelve a iniciar sesion")
                if(it){
                    Snackbar.make(findViewById(R.id.snackbar_view),
                        R.string.invalid_token_message,
                        BaseTransientBottomBar.LENGTH_SHORT).apply {
                        setAction(R.string.accept) {
                            val dialogBuilder = MaterialAlertDialogBuilder(this@MyProfile).apply {
                                setTitle(R.string.logout_confirmation)

                                setPositiveButton(R.string.accept) { dialog, which ->
                                    TokenStorage(application).clearToken()
                                    dialog.dismiss()
                                    startActivity(Intent(this@MyProfile,WelcomeScreenActivity::class.java).also { intent ->
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    })
                                }

                                setNegativeButton(R.string.cancel) { dialog, which ->
                                    dialog.dismiss()
                                }
                            }
                            dialogBuilder.show()
                        }
                        show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val topbar: MaterialToolbar = findViewById(R.id.matTopbar)
        topbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.getProfileData().observe(this) {
            topbar.subtitle = it.username
            try{
                val color = Color.parseColor(it.color)
                topbar.setBackgroundColor(color)
                window.statusBarColor = color

                val colors = Array(262144) {_ -> color }.toIntArray()
                val colorBitmap = Bitmap.createBitmap(colors,512,512,Bitmap.Config.ARGB_8888)
                Palette.from(colorBitmap).generate(){generated ->
                    if(generated?.swatches?.isEmpty() == true) {
                        when(color){
                            Color.WHITE -> {
                                topbar.setTitleTextColor(Color.BLACK)
                                topbar.setSubtitleTextColor(Color.BLACK)
                                topbar.setNavigationIconTint(Color.BLACK)
                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            }
                            Color.BLACK -> {
                                topbar.setTitleTextColor(Color.WHITE)
                                topbar.setSubtitleTextColor(Color.WHITE)
                                topbar.setNavigationIconTint(Color.WHITE)
                            }
                        }
                    } else {
                        generated?.swatches?.first().let { swatch ->
                            swatch.also { swatch ->
                                if (swatch != null) {
                                    topbar.setTitleTextColor(swatch.bodyTextColor)
                                    topbar.setSubtitleTextColor(swatch.bodyTextColor)
                                    topbar.setNavigationIconTint(swatch.bodyTextColor)

                                }

                            }
                        }
                    }

                }
            } catch(ex: IllegalArgumentException){

            }
        }

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout_profile)
        val viewPager = findViewById<ViewPager2>(R.id.profile_main_pager)

        viewModel.getNavigatedTab().observe(this) {tab ->
            viewPager.currentItem = tab
            Log.println(Log.INFO,"ISOLAATTI", tab.toString())
        }

        viewPager.adapter = ProfileTabsAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position) {
                0 -> {
                    tab.text = getString(R.string.profile_info)
                }
                1 -> {
                    tab.text = getString(R.string.discussions)
                }
                2 -> {
                    tab.text = getString(R.string.followers)
                }
                3 -> {
                    tab.text = getString(R.string.following)
                }
            }

        }.attach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("userId",TokenStorage(application).getSessionTokenValidated().userId)
        outState.putBoolean("isValid",TokenStorage(application).getSessionTokenValidated().isValid)
        super.onSaveInstanceState(outState)
    }
}