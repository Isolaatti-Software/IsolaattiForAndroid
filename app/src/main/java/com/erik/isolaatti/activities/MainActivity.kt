/*
* Isolaatti Software, Erik Cavazos, 2022
* Isolaatti Project: MainActivity
*
* This activity decides what to do after application is started.
* If no session is found or it's invalid, then it starts WelcomeScreen,
* otherwise, it starts HomeScreen
*/

package com.erik.isolaatti.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.erik.isolaatti.R
import com.erik.isolaatti.services.TokenStorage
import com.erik.isolaatti.services.WebApiService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = TokenStorage(application).getToken()

        if(token == null) {
            startActivity(Intent(this,WelcomeScreenActivity::class.java))
        } else {
            val dialogBuilder = MaterialAlertDialogBuilder(this).apply {
                setTitle(R.string.unable_to_sign_in)

                setNegativeButton(R.string.accept) { dialog, which ->
                    finish()
                }
            }
            WebApiService(application).validateToken(token.token,{sessionTokenValidated ->

                if(!sessionTokenValidated.isValid){
                    TokenStorage(application).clearToken()
                    startActivity(Intent(this,WelcomeScreenActivity::class.java))
                } else {
                    TokenStorage(application).setSessionTokenValidated(sessionTokenValidated)
                    Log.println(Log.INFO,"ISOLAATTI", sessionTokenValidated.userId.toString())
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                }
            },{ error->

                when (error) {
                    is TimeoutError, is NoConnectionError -> {
                        dialogBuilder.setMessage(R.string.unable_to_connect_server)
                    }
                    is ServerError -> {
                        dialogBuilder.setMessage(R.string.unable_to_sign_in_message)
                    }
                    is AuthFailureError -> {
                        dialogBuilder.setNegativeButton(R.string.accept) { dialog, which ->
                            TokenStorage(application).clearToken()
                            startActivity(Intent(this,WelcomeScreenActivity::class.java))
                        }
                        dialogBuilder.setMessage(R.string.invalid_token_message)
                    }
                }

                dialogBuilder.show()
            })
        }
    }
}