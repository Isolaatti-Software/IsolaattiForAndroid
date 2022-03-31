/*
* Isolaatti Software, Erik Cavazos, 2022
* Isolaatti Project: HomeScreenActivity
*
* This activity is the home screen. It is compose with JetPack Compose, and has the main sections
* This is started from the MainActivity when a valid session is existing.
*/

package com.erik.isolaatti.activities

import android.content.Intent
import com.erik.isolaatti.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

import com.erik.isolaatti.activities.ui.theme.IsolaattiTheme
import com.erik.isolaatti.classes.SessionTokenValidated
import com.erik.isolaatti.services.TokenStorage

class HomeScreenActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState!=null) {
            val sessionTokenValidated = SessionTokenValidated(
                savedInstanceState.getBoolean("isValid"),
                savedInstanceState.getInt("userId")
            )
            TokenStorage(application).setSessionTokenValidated(sessionTokenValidated)
        }

        setContent {
            IsolaattiTheme {
                val drawerState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
                val drawerScope = rememberCoroutineScope()
                ModalBottomSheetLayout(
                    sheetState = drawerState,
                    sheetContent = {
                        ListItem(
                            text = { Text(this@HomeScreenActivity.getString(R.string.profile))},
                            modifier = Modifier.clickable {
                                this@HomeScreenActivity.startActivity(Intent(this@HomeScreenActivity,MyProfile::class.java))
                            }
                        )
                        ListItem(
                            text = { Text(this@HomeScreenActivity.getString(R.string.settings))},
                            modifier = Modifier.clickable {
                                this@HomeScreenActivity.startActivity(Intent(this@HomeScreenActivity,Settings::class.java))
                            }
                        )
                        ListItem(
                            text = { Text(this@HomeScreenActivity.getString(R.string.about))},
                            modifier = Modifier.clickable {

                            }
                        )
                    }
                ) {
                    Scaffold(
                        bottomBar = {
                            BottomNavigation(backgroundColor = MaterialTheme.colors.background, elevation = Dp(10.0F), ){
                                BottomNavigationItem(
                                    icon = { Icon(Icons.Filled.Home, contentDescription = null)},
                                    selected = false,
                                    onClick = {}
                                )
                                BottomNavigationItem(
                                    icon = { Icon(Icons.Filled.Search, contentDescription = null)},
                                    selected = false,
                                    onClick = {}
                                )
                                BottomNavigationItem(
                                    icon = { Icon(Icons.Filled.Notifications, contentDescription = null)},
                                    selected = false,
                                    onClick = {}
                                )
                                BottomNavigationItem(
                                    icon = { Icon(Icons.Filled.Menu, contentDescription = null)},
                                    selected = false,
                                    onClick = {
                                        drawerScope.launch {
                                            drawerState.show()
                                        }
                                    }
                                )
                            }
                        },
                        topBar = { TopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            title= {
                                Text("Isolaatti")
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        this@HomeScreenActivity.startActivity(Intent(this@HomeScreenActivity,MyProfile::class.java)) },
                                    content = {
                                        Icon(Icons.Filled.Person, null)
                                    }
                                )
                            }
                        ) },
                        floatingActionButton = {
                            FloatingActionButton(onClick = {
                                this@HomeScreenActivity.startActivity(Intent(this@HomeScreenActivity,PostEditor::class.java))
                            }){
                                Icon(Icons.Filled.Create, contentDescription = null)
                            }
                        }, floatingActionButtonPosition = FabPosition.Center,
                        isFloatingActionButtonDocked = true
                    ) {

                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("userId", TokenStorage(application).getSessionTokenValidated().userId)
        outState.putBoolean("isValid", TokenStorage(application).getSessionTokenValidated().isValid)
        super.onSaveInstanceState(outState)
    }
}
