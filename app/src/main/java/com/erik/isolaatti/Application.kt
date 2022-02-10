package com.erik.isolaatti

import android.app.Application
import com.erik.isolaatti.services.TokenStorage
import com.erik.isolaatti.services.WebApiService


class Application : Application() {
    override fun onCreate(){
        super.onCreate()
        TokenStorage.setApp(this)
        WebApiService.setApp(this)
    }
}