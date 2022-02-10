package com.erik.isolaatti.activities.ui.signIn

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.erik.isolaatti.classes.SignInData
import com.erik.isolaatti.services.WebApiService

class SignInMainViewModel : ViewModel() {

    val emailIsValid: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val passwordIsValid: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val formIsValid: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun validateEmail() {
        if(email.value == null) {
            emailIsValid.value = false
            return
        }
        val isValid = !TextUtils.isEmpty(email.value!!)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(email.value!!).find()
        emailIsValid.value = isValid
        checkFormValidity()
    }

    fun validatePassword() {
        if(password.value == null) {
            passwordIsValid.value = false
            return
        }
        val isValid = password.value!!.isNotEmpty()
        passwordIsValid.value = isValid
        checkFormValidity()
    }

    private fun checkFormValidity() {
        if(email.value == null || password.value == null){
            formIsValid.postValue(false)
            return
        }
        formIsValid.postValue(emailIsValid.value!! && passwordIsValid.value!!)
    }
}