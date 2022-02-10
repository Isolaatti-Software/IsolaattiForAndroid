package com.erik.isolaatti.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.erik.isolaatti.R
import com.erik.isolaatti.activities.ui.myProfile.MyProfileFragment

class MyProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_profile_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MyProfileFragment.newInstance())
                .commitNow()
        }
    }
}