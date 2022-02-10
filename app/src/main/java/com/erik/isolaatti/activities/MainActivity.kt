package com.erik.isolaatti.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.erik.isolaatti.R
import com.erik.isolaatti.services.TokenStorage
import com.erik.isolaatti.services.WebApiService
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = TokenStorage.getToken()

        if(token == null) {
            startActivity(Intent(this,WelcomeScreenActivity::class.java))
        } else {
            WebApiService.validateToken(token.token,{status ->
                if(status!="ok"){
                    TokenStorage.clearToken()
                } else {
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                }
            },{ error->
                val dialogBuilder = MaterialAlertDialogBuilder(this).apply {
                    setTitle(R.string.unable_to_sign_in)

                    setNegativeButton(R.string.accept) { dialog, which ->
                        finish()
                    }
                }
                when (error) {
                    is TimeoutError, is NoConnectionError -> {
                        dialogBuilder.setMessage(R.string.unable_to_connect_server)
                    }
                    is ServerError -> {
                        dialogBuilder.setMessage(R.string.unable_to_sign_in_message)
                    }
                    is AuthFailureError -> {
                        dialogBuilder.setNegativeButton(R.string.accept) { dialog, which ->
                            TokenStorage.clearToken()
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