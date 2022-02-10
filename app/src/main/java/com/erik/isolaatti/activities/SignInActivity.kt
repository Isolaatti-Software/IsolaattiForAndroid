package com.erik.isolaatti.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.erik.isolaatti.R
import com.erik.isolaatti.activities.ui.signIn.SignInFragment

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SignInFragment.newInstance())
                .commitNow()
        }
    }
}