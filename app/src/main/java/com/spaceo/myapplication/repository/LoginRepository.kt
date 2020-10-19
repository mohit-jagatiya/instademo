package com.spaceo.myapplication.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.spaceo.myapplication.insdataselection.network.RestClient
import com.google.gson.Gson
import com.spaceo.myapplication.insdataselection.model.InstagramAccesTokenModel
import com.spaceo.myapplication.insdataselection.model.InstagramUserDetailsModel
import com.spaceo.myapplication.utils.BASE_URL_INSTAGRAM_FOR_ACCESS_TOKEN
import com.spaceo.myapplication.utils.BASE_URL_INSTAGRAM_FOR_USER_DETAILS
//import com.spaceo.myapplication.webservice.ApiClient
//import com.spaceo.myapplication.webservice.api.AuthenticationApi
//import com.spaceo.myapplication.webservice.api.UserMasterResponse
import com.spaceo.myapplication.factory.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {

    fun getInstaramUserIDAndAccessToken(appID: String, appSecret: String, grantType: String, redirectUri: String, code: String):MutableLiveData<Any> {
        val data = MutableLiveData<Any>()

        RestClient.retrofitClient(BASE_URL_INSTAGRAM_FOR_ACCESS_TOKEN).getAccessTokenFromInstagramAccount(appID, appSecret, grantType, redirectUri, code).enqueue(object : Callback<InstagramAccesTokenModel> {
            override fun onFailure(call: Call<InstagramAccesTokenModel>?, t: Throwable?) {
                t!!.printStackTrace()
                Log.e("TAG", "Loading:onFailure false")
                data.value = Resource.Loading<Boolean>(false)
                data.value = Resource.Error<String>("No Internet Message")
            }

            override fun onResponse(
                call: Call<InstagramAccesTokenModel>?,
                response: Response<InstagramAccesTokenModel>?) {

                if (response!!.isSuccessful) {
                    val mBean = response.body()!!
                    if (mBean.user_id.toString().isNotEmpty() && mBean.access_token.isNotEmpty()) {
                        data.value = Resource.Success(mBean)
                    } else {
                        data.value = Resource.Error<String>(response.message().toString())
                    }
                }
            }
        })
        return data
    }


    fun getUserDetailsFromInstagram(userID: String, accessToken: String): MutableLiveData<Any> {

        val data = MutableLiveData<Any>()

        RestClient.retrofitClient(BASE_URL_INSTAGRAM_FOR_USER_DETAILS).getUserDetailsFromInstagram(userID, "id,username,profile_picture", accessToken).enqueue(object : Callback<InstagramUserDetailsModel> {
            override fun onFailure(call: Call<InstagramUserDetailsModel>?, t: Throwable?) {
                data.value = Resource.Error<String>(t!!.message.toString())
            }

            override fun onResponse(call: Call<InstagramUserDetailsModel>?, response: Response<InstagramUserDetailsModel>?) {

                if (response!!.isSuccessful) {
                    data.value = Resource.Success(response.body()!!)

                } else {
                    data.value = Resource.Error<String>(
                        Gson().toJson(response.errorBody()))

                }
            }
        })

        return data

    }

}