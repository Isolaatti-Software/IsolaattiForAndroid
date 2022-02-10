package com.erik.isolaatti.services

import com.erik.isolaatti.Application
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.erik.isolaatti.classes.SessionToken
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

object TokenStorage {
    private var decodedToken: SessionToken? = null

    private lateinit var app: Application

    fun setApp(application: Application){
        app = application
    }

    /*
    * Try to decrypt token from internal storage, put it in memory and returns it
    * */
    private fun decryptToken(): String {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val fileToRead = "authdata.isolaatti"
        val encryptedFile = EncryptedFile.Builder(
            File(app.applicationContext.filesDir, fileToRead),
            app.applicationContext,
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return try {
            val inputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = inputStream.read()
            while(nextByte != -1){
                byteArrayOutputStream.write(nextByte)
                nextByte = inputStream.read()
            }

            val plainText = byteArrayOutputStream.toByteArray().decodeToString()
            decodedToken = Gson().fromJson(plainText,SessionToken::class.java)

            plainText
        } catch(exception: IOException) {
            ""
        }

    }

    private fun encryptToken(tokenJson: String){

        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val fileToWrite = "authdata.isolaatti"
        val encryptedFile = EncryptedFile.Builder(
            File(app.applicationContext.filesDir,fileToWrite),
            app.applicationContext,
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val fileContent = tokenJson
            .toByteArray(StandardCharsets.UTF_8)
        encryptedFile.openFileOutput().apply {
            write(fileContent)
            flush()
            close()
        }
    }

    private fun deleteFile(){
        try {
            File(app.applicationContext.filesDir, "authdata.isolaatti").delete()
        } catch(ex: IOException) {

        }
    }

    /*
    * Encode token and store it in memory and internal storage
    */
    fun setToken(token: SessionToken){
        val jsonEncoded = Gson().toJson(token)
        decodedToken = token;
        encryptToken(jsonEncoded)
    }

    fun getToken():SessionToken? {
        if(decodedToken == null) {
            decryptToken()
        }
        return decodedToken
    }

    fun clearToken(){
        decodedToken = null
        deleteFile()
    }

}