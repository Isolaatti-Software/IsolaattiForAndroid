package com.erik.isolaatti.activities.ui.myProfile

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.erik.isolaatti.R
import com.erik.isolaatti.activities.EditProfile
import com.erik.isolaatti.classes.ProfileData
import com.erik.isolaatti.services.TokenStorage
import kotlinx.coroutines.flow.asStateFlow

class ProfileInfoProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ProfileInfo()
            }
        }
    }

    @OptIn(ExperimentalUnitApi::class)
    @Composable
    fun ProfileInfo(){
        fun Context.findActivity(): Activity? = when(this) {
            is Activity -> this
            is ContextWrapper -> baseContext.findActivity()
            else -> null
        }

        val viewModel: ProfileViewModel = viewModel(requireActivity())
        val profileId = viewModel.profileId
        val isLoading = viewModel.isLoadingProfile.collectAsState()

        // If userId is passed and it's not the same as this user then I will fetch that profile,
        // otherwise my profile
        val userInfo = viewModel.getProfileData().observeAsState()

        Column(modifier = Modifier.verticalScroll(state = rememberScrollState(), enabled = true)) {
            if(isLoading.value){
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if(profileId.value == TokenStorage.getSessionTokenValidated().userId || profileId.value == -1){
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = {
                            startActivity(Intent(requireContext(),EditProfile::class.java))
                        },
                        content = {Icon(Icons.Filled.Edit,null)}
                    )
                }
            }
            userInfo.value?.let {profile ->

                Card(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).then(Modifier.fillMaxWidth())) {
                    Column {
                        Row(modifier = Modifier.padding(start = 20.dp), verticalAlignment = Alignment.Top){
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current).data(profile.profilePictureUrl)
                                        .apply(block = fun ImageRequest.Builder.() {
                                            transformations(CircleCropTransformation())
                                        }).build()
                                ),
                                modifier = Modifier.width(120.dp).then(Modifier.height(120.dp).padding(vertical = 8.dp)),
                                contentDescription = null
                            )
                            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
                                userInfo.value?.let {profileData ->
                                    Text(profileData.username,
                                        fontSize = TextUnit(value = 20.0F, type = TextUnitType.Sp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    profileData.email?.let {
                                        Text(it)
                                    }
                                    Text("${stringResource(R.string.discussions)}: ${profileData.numberOfPosts}")
                                    Text("${stringResource(R.string.number_of_likes)}: ${profileData.numberOfLikes}")
                                }

                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Button(
                                onClick = {  },
                                border = null,
                                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                                colors = ButtonDefaults.textButtonColors(),
                                content = { Text(text = "${stringResource(R.string.followers)}: ${profile.numberOfFollowers}") }
                            )
                            Button(
                                onClick = { },
                                border = null,
                                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                                colors = ButtonDefaults.textButtonColors(),
                                content = { Text(text = "${stringResource(R.string.following)}: ${profile.numberOfFollowings}") }
                            )
                        }
                    }
                }
                profile.audioUrl?.let {
                    Card(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).then(Modifier.fillMaxWidth())) {
                        Text(
                            modifier = Modifier.padding(16.dp).then(Modifier.fillMaxWidth()),
                            text = it
                        )
                    }
                }
                profile.description?.let { it ->
                    Card(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).then(Modifier.fillMaxWidth())) {
                        Text(
                            modifier = Modifier.padding(16.dp).then(Modifier.fillMaxWidth()),
                            text = it
                        )
                    }
                }

            }
        }
    }
}