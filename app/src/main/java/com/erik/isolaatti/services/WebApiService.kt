/*
* Isolaatti Software, Erik Cavazos, 2022
* Isolaatti Project: WebApiService
*/

package com.erik.isolaatti.services

import android.app.Application
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

import com.erik.isolaatti.classes.*

/*
* This class is a custom implementation of Volley Request. It uses Gson to automatically
* parse and return objects of the specified type.
*/
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
            Log.println(Log.INFO,"ISOLAATTI", json)
            Response.success(
                gson.fromJson(json, dataClass),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch(e: UnsupportedEncodingException){
            Response.error(ParseError(e))
        } catch(e: JsonSyntaxException){
            Response.error(ParseError(e))
        } catch(e: Error) {
            Response.error(ParseError(e))
        }
    }

    override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
        return super.parseNetworkError(volleyError)
    }
}

/*
* This is a class (and singleton object) that holds all WebAPI access layer. It has methods
* that receive some parameters and then create the request. It also has the global request queue, so
* that there is no need to create a new queue each time.
*/
object WebApiService {
    private lateinit var application: Application
    private var requestQueue: RequestQueue? = null
    private var headers: MutableMap<String,String>? = null
    private const val host = "https://isolaatti.azurewebsites.net"

    /*
    * This "operator overload" is to allow this object to be invoked (using parenthesis) so that I can
    * pass the application instance and then get the application context
    */
    operator fun invoke(application: Application): WebApiService{
        this.application = application
         when {
            TokenStorage(application).getToken() == null -> headers = null
            TokenStorage(application).getToken() != null -> {
                val headers:MutableMap<String,String> = HashMap()
                val token = TokenStorage(WebApiService.application).getToken()?.token!!
                headers["sessionToken"] = token
                this.headers = headers
                validateToken(token,{tokenValidated ->
                    TokenStorage(application).setSessionTokenValidated(tokenValidated)
                },{

                })
            }
            else -> headers = null
        }

        return this
    }

    private fun getRequestQueue(): RequestQueue {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application.applicationContext)
        }
        return requestQueue!!
    }

    fun getMyProfile(listener: Response.Listener<ProfileData>, errorListener: Response.ErrorListener){
        val request = GsonRequest(Request.Method.GET,
            "${host}/api/Fetch/MyProfile",
            ProfileData::class.java,
            headers,listener,null,errorListener)

        getRequestQueue().add(request)
    }

    fun getMyPosts(userId: Int,listener: Response.Listener<Array<Post>>, errorListener: Response.ErrorListener) {
        val request = GsonRequest(Request.Method.GET,
        "${host}/api/Fetch/PostsOfUser/${userId}",
        Array<Post>::class.java,
        headers,listener,null,errorListener)

        getRequestQueue().add(request)
    }

    fun getFollowers(userId: Int, listener:Response.Listener<Array<UserDataOnFollowingLists>>, errorListener: Response.ErrorListener){
        val request = GsonRequest(Request.Method.GET,
        "${host}/api/Following/FollowersOf/${userId}",
        Array<UserDataOnFollowingLists>::class.java,
        headers,listener,null,errorListener)

        getRequestQueue().add(request)
    }

    fun getFollowings(userId: Int, listener:Response.Listener<Array<UserDataOnFollowingLists>>, errorListener: Response.ErrorListener){
        val request = GsonRequest(Request.Method.GET,
            "${host}/api/Following/FollowingsOf/${userId}",
            Array<UserDataOnFollowingLists>::class.java,
            headers,listener,null,errorListener)

        getRequestQueue().add(request)
    }

    fun getAProfile(userId: Int, listener: Response.Listener<ProfileData>, errorListener: Response.ErrorListener){
        val requestData = SingleIdentification(userId.toLong())
        val request = GsonRequest(Request.Method.POST,"${host}/api/Fetch/UserProfile",ProfileData::class.java,
            headers,listener,requestData,errorListener)

        getRequestQueue().add(request)
    }

    fun validateToken(token: String, listener: Response.Listener<SessionTokenValidated>, errorListener: Response.ErrorListener){

        val headers = HashMap<String,String>()
        headers["sessionToken"] = token
        val request = GsonRequest(Request.Method.POST,
            "${host}/api/LogIn/Verify",
            SessionTokenValidated::class.java,
            headers,
            listener,
            null,
            errorListener)

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