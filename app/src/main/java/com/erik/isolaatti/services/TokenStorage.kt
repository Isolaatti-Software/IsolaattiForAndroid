/*
* Isolaatti Software, Erik Cavazos, 2022
* Isolaatti Project: TokenStorageService
*/

package com.erik.isolaatti.services

import android.app.Application
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

import com.erik.isolaatti.classes.SessionToken
import com.erik.isolaatti.classes.SessionTokenValidated
import java.security.GeneralSecurityException

object TokenStorage {
    private var decodedToken: SessionToken? = null

    private lateinit var app: Application

    /*
    * This "operator overload" is to allow this object to be invoked (using parenthesis) so that I can
    * pass the application instance and then get the application context
    */
    operator fun invoke(application: Application): TokenStorage {
        this.app = application
        return this
    }


    /*This stores the response from the server that indicates if the token is valid or not */
    private lateinit var tokenValidated: SessionTokenValidated

    fun setSessionTokenValidated(sessionTokenValidated: SessionTokenValidated) {
        tokenValidated = sessionTokenValidated
    }

    fun getSessionTokenValidated(): SessionTokenValidated = tokenValidated

    /*
    * Try to decrypt token from internal storage, put it in memory and returns it
    * */
    private fun decryptToken(): String? {
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
            decodedToken = null
            null
        } catch(exception: GeneralSecurityException) {
            decodedToken = null
            clearToken()
            null
        }

    }

    /*
    * This method encrypts and writes the token to the internal storage.
    * */
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