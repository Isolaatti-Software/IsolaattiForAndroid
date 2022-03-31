package com.erik.isolaatti.activities.ui.myProfile

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erik.isolaatti.ComposerWidgets.Post
import com.erik.isolaatti.R
import com.erik.isolaatti.classes.Post
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


class ProfilePostsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile_posts, container, false).apply {
            findViewById<ComposeView>(R.id.compose).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    ListOfPosts()
                }
            }
        }
    }
    @Composable
    fun ListOfPosts() {
        val viewModel: ProfileViewModel = viewModel(requireActivity())
        val isRefreshing by viewModel.isRefreshingPosts.collectAsState()
        val posts = viewModel.getPosts().observeAsState()
        Column {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    viewModel.fetchPosts()
                }
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    if(posts.value != null) {
                        if(posts.value!!.isEmpty()){
                            item {
                                Text(stringResource(R.string.no_posts_to_show))
                            }
                        }
                        items(posts.value!!) { post ->
                            Post(post)
                        }
                    }
                }
            }
        }
    }
}

