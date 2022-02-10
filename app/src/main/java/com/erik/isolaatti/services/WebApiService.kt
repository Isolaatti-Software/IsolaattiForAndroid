package com.erik.isolaatti.services

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.erik.isolaatti.Application
import com.erik.isolaatti.classes.ProfileData
import com.erik.isolaatti.classes.SessionToken
import com.erik.isolaatti.classes.SignInData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class GsonRequest<T>(
    method: Int,
    url: String,
    private val dataClass: Class<T>,
    private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<T>,
    private val jsonRequestData: Any?,
    errorListener: Response.ErrorListener
) : Request<T>(method, url, errorListener) {

    private val gson = Gson()
    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun getBodyContentType(): String = "application/json"

    override fun getBody(): ByteArray {
        return gson.toJson(jsonRequestData).toByteArray()
    }

    override fun deliverResponse(response: T) {
        listener.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            val json = String(response?.data ?: ByteArray(0), Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
            Response.success(
                gson.fromJson(json, dataClass),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch(e: UnsupportedEncodingException){
            Response.error(ParseError(e))
        } catch(e: JsonSyntaxException){
            Response.error(ParseError(e))
        }
    }
}

object WebApiService {
    private lateinit var app: Application
    private var requestQueue: RequestQueue? = null
    private lateinit var headers: MutableMap<String,String>

    val host = "https://isolaatti.azurewebsites.net"

    fun setApp(application: Application){
        app = application
    }

    fun setDefaultHeaders(defaultHeaders: MutableMap<String, String>){
        headers = defaultHeaders
    }

    private fun getRequestQueue(): RequestQueue {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(app.applicationContext)
        }
        return requestQueue!!
    }

    fun getMyProfile(userId: Int, listener: Response.Listener<ProfileData>, errorListener: Response.ErrorListener){
        val request = GsonRequest(Request.Method.GET,
            "${host}/api/Fetch/MyProfile",
            ProfileData::class.java,
            headers,listener,null,errorListener)

        getRequestQueue().add(request)
    }

    fun validateToken(token: String, listener: Response.Listener<String>, errorListener: Response.ErrorListener){
        val request = object: StringRequest(Request.Method.POST, "${host}/api/LogIn/Verify", listener,errorListener){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["sessionToken"] = token
                return headers
            }
        }

        getRequestQueue().add(request)
    }

    fun signIn(email:String, password: String, listener: Response.Listener<SessionToken>, errorListener: Response.ErrorListener) {
        val request = GsonRequest(
            Request.Method.POST,
            "${host}/api/LogIn",
            SessionToken::class.java,
            null,
            listener,
            SignInData(email,password),
            errorListener)
        getRequestQueue().add(request)
    }

}

