package com.erik.isolaatti.activities

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.erik.isolaatti.R
import com.erik.isolaatti.activities.ui.theme.IsolaattiTheme
import com.erik.isolaatti.classes.ProfileData
import com.erik.isolaatti.services.WebApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    val username: MutableState<String> = mutableStateOf("")
    val email: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val profileImageUrl: MutableState<String> = mutableStateOf("")

    private val profileData: MutableLiveData<ProfileData> by lazy {
        MutableLiveData<ProfileData>().also {
            fetchProfileData()
        }
    }


    private val _isLoadingProfile = MutableStateFlow(false)

    private fun fetchProfileData() {
        viewModelScope.launch {
            _isLoadingProfile.emit(true)
        }
        WebApiService(getApplication()).getMyProfile({ profileData ->
            username.value = profileData.username
            email.value = profileData.email.toString()
            description.value = profileData.description.toString()
            this.profileData.value = profileData
            viewModelScope.launch {
                _isLoadingProfile.emit(false)
            }
        },{

        })

    }

    fun getProfileData(): LiveData<ProfileData> = profileData
    val isLoadingProfile: StateFlow<Boolean>
        get() = _isLoadingProfile
}

class EditProfile : AppCompatActivity() {

    lateinit var viewModel: EditProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]

        setContent {
            IsolaattiTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            title= { Text(getString(R.string.edit_profile)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { finish() },
                                    content = { Icon(Icons.Filled.Close,null) }
                                ) },
                            actions = {
                                IconButton(
                                    onClick = {},
                                    content = { Icon(Icons.Filled.Done, null) }
                                )
                            }
                        )
                    }
                ) {
                    EditProfile()
                }
            }
        }
    }

    @Composable
    fun EditProfile() {
        var username by rememberSaveable { viewModel.username }
        var email by rememberSaveable() { viewModel.email }
        var description by rememberSaveable { viewModel.description }
        var urlImage by rememberSaveable { viewModel.profileImageUrl }
        val profile = viewModel.getProfileData().observeAsState()


        Column(modifier = Modifier.verticalScroll(state = rememberScrollState(), enabled = true)) {
            profile.value?.let{
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().then(Modifier.padding(vertical = 8.dp))) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current).data(it.profilePictureUrl)
                                .apply(block = fun ImageRequest.Builder.() {
                                    transformations(CircleCropTransformation())
                                }).build()
                        ),
                        modifier = Modifier.width(120.dp).then(Modifier.height(120.dp).padding(vertical = 8.dp)),
                        contentDescription = null
                    )
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().then(Modifier.padding(8.dp)),

                    value = username,
                    onValueChange = { username = it},
                    singleLine = true,
                    label = { Text("Username")}

                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().then(Modifier.padding(8.dp)),

                    value = email,
                    onValueChange = { email = it},
                    singleLine = true,
                    label = { Text("Email")}

                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().then(Modifier.padding(8.dp)),

                    value = description,
                    onValueChange = { description = it},
                    singleLine = false,
                    label = { Text("Description")}
                )
            }
        }
    }

}

