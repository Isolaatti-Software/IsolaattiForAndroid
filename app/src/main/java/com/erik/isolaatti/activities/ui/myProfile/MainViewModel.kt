package com.erik.isolaatti.activities.ui.myProfile

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.erik.isolaatti.classes.Post
import com.erik.isolaatti.classes.ProfileData
import com.erik.isolaatti.classes.UserDataOnFollowingLists
import com.erik.isolaatti.services.TokenStorage
import com.erik.isolaatti.services.WebApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val profileData: MutableLiveData<ProfileData> by lazy {
        MutableLiveData<ProfileData>().also {
            fetchProfileData()
        }
    }

    private val myPosts: MutableLiveData<Array<Post>> by lazy {
        MutableLiveData<Array<Post>>().also {
            fetchPosts()
        }
    }

    private val myFollowers: MutableLiveData<Array<UserDataOnFollowingLists>> by lazy {
        MutableLiveData<Array<UserDataOnFollowingLists>>().also {
            fetchFollowers(if(profileId.value == -1){TokenStorage.getSessionTokenValidated().userId} else {profileId.value})
        }
    }

    private val myFollowings: MutableLiveData<Array<UserDataOnFollowingLists>> by lazy {
        MutableLiveData<Array<UserDataOnFollowingLists>>().also {
            fetchFollowings(if(profileId.value == -1){TokenStorage.getSessionTokenValidated().userId} else {profileId.value})
        }
    }

    val navigatedTab: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            it.value = 0
        }
    }

    fun getNavigatedTab(): LiveData<Int> = navigatedTab

    private val _isRefreshingPosts = MutableStateFlow(false)
    private val _isRefreshingMyFollowers = MutableStateFlow(false)
    private val _isRefreshingMyFollowings = MutableStateFlow(false)
    private val _isLoadingProfile = MutableStateFlow(false)
    private val _noInternetConnectionErrorOccurred = MutableStateFlow(false)
    private val _signInAgainError = MutableStateFlow(false)
    private val _connectionTimedOut = MutableStateFlow(false)
    val profileId = mutableStateOf(-1)



    private fun fetchProfileData() {
        viewModelScope.launch {
            _isLoadingProfile.emit(true)
        }
        if(profileId.value == -1){
            WebApiService(getApplication()).getMyProfile({profileData ->
                this.profileData.postValue(profileData)
                viewModelScope.launch {
                    _isLoadingProfile.emit(false)
                }
            },{ error ->
                when(error) {
                    is AuthFailureError -> viewModelScope.launch {
                        _signInAgainError.emit(true)
                    }
                    is TimeoutError -> viewModelScope.launch {
                        _connectionTimedOut.emit(true)
                    }
                    is NoConnectionError -> viewModelScope.launch {
                        _noInternetConnectionErrorOccurred.emit(true)
                    }
                }

            })
        } else {
            Log.println(Log.INFO,"ISOLAATTI","Id del perfil " + profileId.value.toString())
            WebApiService(getApplication()).getAProfile(profileId.value,{profileData ->
                this.profileData.postValue(profileData)
                viewModelScope.launch {
                    _isLoadingProfile.emit(false)
                }
            },{ error ->
                when(error) {
                    is AuthFailureError -> viewModelScope.launch {
                        _signInAgainError.emit(true)
                    }
                    is TimeoutError -> viewModelScope.launch {
                        _connectionTimedOut.emit(true)
                    }
                    is NoConnectionError -> viewModelScope.launch {
                        _noInternetConnectionErrorOccurred.emit(true)
                    }
                }
            })
        }



    }

    fun fetchPosts() {
        viewModelScope.launch {
            _isRefreshingPosts.emit(true)
        }
        WebApiService(getApplication()).getMyPosts(
            if(profileId.value == -1){
                TokenStorage.getSessionTokenValidated().userId }
            else {
                profileId.value
            },
            {posts ->
                myPosts.postValue(posts)
                viewModelScope.launch {
                    _isRefreshingPosts.emit(false)
                }
            }, {error ->
                when(error) {
                    is AuthFailureError -> viewModelScope.launch {
                        _signInAgainError.emit(true)
                    }
                    is TimeoutError -> viewModelScope.launch {
                        _connectionTimedOut.emit(true)
                    }
                    is NoConnectionError -> viewModelScope.launch {
                        _noInternetConnectionErrorOccurred.emit(true)
                    }
                }
                Log.println(Log.ERROR,"ISOLAATTI", error.message.toString() + error.stackTraceToString())
            }
        )
    }

    fun fetchFollowers(userId: Int) {
        viewModelScope.launch {
            _isRefreshingMyFollowers.emit(true)
        }
        WebApiService(getApplication()).getFollowers(userId,{users ->
            myFollowers.postValue(users)
            viewModelScope.launch {
                _isRefreshingMyFollowers.emit(false)
            }
        },{ error ->
            when(error) {
                is AuthFailureError -> viewModelScope.launch {
                    _signInAgainError.emit(true)
                }
                is TimeoutError -> viewModelScope.launch {
                    _connectionTimedOut.emit(true)
                }
                is NoConnectionError -> viewModelScope.launch {
                    _noInternetConnectionErrorOccurred.emit(true)
                }
            }
        })
    }

    fun fetchFollowings(userId: Int) {
        viewModelScope.launch {
            _isRefreshingMyFollowings.emit(true)
        }
        WebApiService(getApplication()).getFollowings(userId, {users ->
            myFollowings.postValue(users)
            viewModelScope.launch {
                _isRefreshingMyFollowings.emit(false)
            }
        }, { error ->
            when(error) {
                is AuthFailureError -> viewModelScope.launch {
                    _signInAgainError.emit(true)
                }
                is TimeoutError -> viewModelScope.launch {
                    _connectionTimedOut.emit(true)
                }
                is NoConnectionError -> viewModelScope.launch {
                    _noInternetConnectionErrorOccurred.emit(true)
                }
            }
        })
    }

    fun getProfileData(): LiveData<ProfileData> = profileData
    fun getPosts(): LiveData<Array<Post>> = myPosts
    fun getFollowers(): LiveData<Array<UserDataOnFollowingLists>> = myFollowers
    fun getFollowings(): LiveData<Array<UserDataOnFollowingLists>> = myFollowings
    val isRefreshingPosts: StateFlow<Boolean>
        get() = _isRefreshingPosts
    val isRefreshingMyFollowers: StateFlow<Boolean>
        get() = _isRefreshingMyFollowers
    val isRefreshingMyFollowings: StateFlow<Boolean>
        get() = _isRefreshingMyFollowings
    val isLoadingProfile: StateFlow<Boolean>
        get() = _isLoadingProfile
    val noInternetConnectionError: StateFlow<Boolean>
        get() = _noInternetConnectionErrorOccurred
    val connectionTimedOut: StateFlow<Boolean>
        get() = _connectionTimedOut
    val signInAgain: StateFlow<Boolean>
        get() = _signInAgainError
}