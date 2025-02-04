package com.example.trimmix

import android.util.Base64
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NetworkService {

    private val clientId = "2743767e85b244ce8edf99e4840fa812"
    private val clientSecret = "416a2db1bd91484ab0198567e7d05548"

    private val okHttpClient = OkHttpClient()

    // Request to get the access token from Spotify
    fun getAccessToken(callback: (String?) -> Unit) {
        val credentials = "$clientId:$clientSecret"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val requestBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .header("Authorization", "Basic $base64Credentials")
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val accessToken = JSONObject(responseBody).getString("access_token")
                    callback(accessToken)
                } else {
                    callback(null) // If request fails, return null
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null) // Handle network failure
            }
        })
    }
}
