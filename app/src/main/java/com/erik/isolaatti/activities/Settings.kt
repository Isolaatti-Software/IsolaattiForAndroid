package com.erik.isolaatti.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContentProviderCompat.requireContext
import com.erik.isolaatti.activities.ui.theme.IsolaattiTheme
import com.erik.isolaatti.services.TokenStorage
import com.erik.isolaatti.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Settings : AppCompatActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IsolaattiTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Text(getString(R.string.settings))
                        })
                    }
                ) {
                    Column {
                        ListItem(
                            text = {
                                Text("Security")
                            }
                        )
                        ListItem(
                            text = {
                                Text("Privacy")
                            }
                        )
                        ListItem(
                            text = {
                                Text("Your data")
                            }
                        )
                        ListItem(text = {
                            Text("Log me out")
                        }, modifier = Modifier.clickable {
                            val dialogBuilder = MaterialAlertDialogBuilder(this@Settings).apply {
                                setTitle(R.string.logout_confirmation)

                                setPositiveButton(R.string.accept) { dialog, which ->
                                    TokenStorage(application).clearToken()
                                    dialog.dismiss()
                                    startActivity(Intent(this@Settings,WelcomeScreenActivity::class.java).also {
                                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    })
                                }

                                setNegativeButton(R.string.cancel) { dialog, which ->
                                    dialog.dismiss()
                                }
                            }
                            dialogBuilder.show()

                        })
                    }
                }
            }
        }
    }
}