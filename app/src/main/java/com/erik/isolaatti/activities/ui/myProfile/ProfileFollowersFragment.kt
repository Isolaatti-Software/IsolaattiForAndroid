package com.erik.isolaatti.activities.ui.myProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erik.isolaatti.ComposerWidgets.UserListItem
import com.erik.isolaatti.R
import com.erik.isolaatti.services.TokenStorage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


class ProfileFollowersFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_followers,null).apply {
            findViewById<ComposeView>(R.id.profileFollowersTabComposeView).setContent {
                UserList()
            }
        }
    }

    @Composable
    fun UserList(){
        val viewModel: ProfileViewModel = viewModel(viewModelStoreOwner = requireActivity())
        val isRefreshing by viewModel.isRefreshingMyFollowers.collectAsState()
        val followers = viewModel.getFollowers().observeAsState()
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                viewModel.fetchFollowers(if(viewModel.profileId.value == -1){
                    TokenStorage.getSessionTokenValidated().userId} else {viewModel.profileId.value})
            }) {
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                if(followers.value != null) {
                    items(followers.value!!) {follower ->
                        UserListItem(follower)
                    }
                }
            }
        }
    }
}

