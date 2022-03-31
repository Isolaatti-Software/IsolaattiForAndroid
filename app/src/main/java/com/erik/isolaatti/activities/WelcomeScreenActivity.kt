package com.erik.isolaatti.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.erik.isolaatti.R
import com.erik.isolaatti.services.WebApiService
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class WelcomeScreenActivity : AppCompatActivity() {
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()){res ->
        this.onSignInResult(res)
    }

    private val isolaattiLoginLauncher = registerForActivityResult(StartActivityForResult()){ res: ActivityResult ->
        this.onIsolaattiSignInResult(res)
    }

    private lateinit var signInWithOtherMethodsButton: Button
    private lateinit var signInWithEmailAndPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
    }

    override fun onStart() {
        super.onStart()



        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.MicrosoftBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher)
            .build()

        signInWithOtherMethodsButton = findViewById(R.id.signInWithOtherMethodsBtn)
        signInWithOtherMethodsButton.setOnClickListener {
            it.isEnabled = false
            signInLauncher.launch(signInIntent)
        }

        signInWithEmailAndPasswordButton = findViewById(R.id.signInWithEmailAndPasswordButton)
        signInWithEmailAndPasswordButton.setOnClickListener {
            it.isEnabled = false
            isolaattiLoginLauncher.launch(Intent(this,SignInActivity::class.java))
        }
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        when(result.resultCode){
            Activity.RESULT_CANCELED -> {
                signInWithOtherMethodsButton.isEnabled = true
            }
            Activity.RESULT_OK -> {
                Toast.makeText(this,result.idpResponse?.email, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onIsolaattiSignInResult(result: ActivityResult) {


        when(result.resultCode) {
            Activity.RESULT_CANCELED -> signInWithEmailAndPasswordButton.isEnabled = true
            Activity.RESULT_OK -> {
                val token = result.data?.getStringExtra("token")
                Toast.makeText(this,R.string.signing_in,Toast.LENGTH_SHORT).show()
                signInWithEmailAndPasswordButton.isEnabled = false
                // Here it's known that the Isolaatti's sign in process was successful and
                // a token is returned.

                WebApiService(application).validateToken(token!!, {state ->
                    if(state.isValid){
                        val homeScreenIntent = Intent(this, HomeScreenActivity::class.java)
                        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(homeScreenIntent)
                    }

                },{
                    if(it.networkResponse.statusCode == 401) {
                        // Verification successful, but token is not valid
                        removeToken()
                    } else {
                        // A server or network occurred
                        Toast.makeText(this, "Error " + it.networkResponse.statusCode, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun removeToken(){

    }
}