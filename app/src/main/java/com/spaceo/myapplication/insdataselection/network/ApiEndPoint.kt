package com.spaceo.myapplication.insdataselection.network

import com.spaceo.myapplication.insdataselection.model.InstagramAccesTokenModel
import com.spaceo.myapplication.insdataselection.model.InstagramUserDetailsModel
import retrofit2.Call
import retrofit2.http.*


interface ApiEndPoint {

    /**This api for get access token from Instagram account*/
    @FormUrlEncoded
    @POST("oauth/access_token")
    fun getAccessTokenFromInstagramAccount(
        @Field("app_id") app_id: String,
        @Field("app_secret") app_secret: String,
        @Field("grant_type") grant_type: String,
        @Field("redirect_uri") redirect_uri: String,
        @Field("code") code: String
    ): Call<InstagramAccesTokenModel>

    /**This api for get User details from Instagram account*/
    @GET("{user-id}")
    fun getUserDetailsFromInstagram(
        @Path("user-id") userId: String,
        @Query("fields") fields: String,
        @Query("access_token") accessToken: String
    ): Call<InstagramUserDetailsModel>

}