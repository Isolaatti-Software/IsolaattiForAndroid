package com.erik.isolaatti.activities.ui.signIn

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.erik.isolaatti.R
import com.erik.isolaatti.classes.SignInData
import com.erik.isolaatti.services.TokenStorage
import com.erik.isolaatti.services.WebApiService
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignInFragment : Fragment() {

    companion object {
        fun newInstance() = SignInFragment()
    }

    private lateinit var viewModel: SignInMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.sign_in_main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignInMainViewModel::class.java)

        val emailFormField: TextInputLayout? = activity?.findViewById(R.id.signInEmailFormField)
        val passwordFormField: TextInputLayout? = activity?.findViewById(R.id.signInPasswordFormField)
        val signInButton: Button? = activity?.findViewById(R.id.signInButton)

        emailFormField?.editText?.doOnTextChanged { text, start, before, count ->
            viewModel.email.value = emailFormField.editText?.text.toString()
            viewModel.validateEmail()
        }

        passwordFormField?.editText?.doOnTextChanged { text, start, before, count ->
            viewModel.password.value = passwordFormField.editText?.text.toString()
            viewModel.validatePassword()
        }

        signInButton?.isEnabled = false

        viewModel.emailIsValid.observe(viewLifecycleOwner) { isValid ->
            if(!isValid) {
                emailFormField?.error = getString(R.string.email_is_not_valid)
            } else {
                emailFormField?.error = null
            }
        }


        viewModel.passwordIsValid.observe(viewLifecycleOwner) {isValid ->
            if(!isValid) {
                passwordFormField?.error = getString(R.string.password_is_invalid)
            } else {
                passwordFormField?.error = null
            }
        }

        viewModel.formIsValid.observe(viewLifecycleOwner) {formIsValid ->
            signInButton?.isEnabled = formIsValid
        }

        signInButton?.setOnClickListener {
            Log.println(Log.ERROR,"click","Click en iniciar sesión")
            WebApiService.signIn(viewModel.email.value!!,viewModel.password.value!!,{sessionToken ->
                TokenStorage.setToken(sessionToken)
                Log.println(Log.ERROR,"click","Se pidio el token")
                val headers:MutableMap<String,String> = HashMap()
                headers["sessionToken"] = sessionToken.token
                WebApiService.setDefaultHeaders(headers)

                val resultIntent = Intent()
                resultIntent.putExtra("token", sessionToken.token)
                activity?.setResult(Activity.RESULT_OK,resultIntent)
                activity?.finish()
            },{error->
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            })


        }
    }


}