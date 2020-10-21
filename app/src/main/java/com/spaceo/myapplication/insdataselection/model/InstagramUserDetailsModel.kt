package com.spaceo.myapplication.insdataselection.model

data class InstagramUserDetailsModel(
    val error: Error,
    val id: String,
    val username: String,
    val name: String,
    val profile_picture_url_hd:String,
    val followers_count:String,
    val follows_count: String
)
{
    data class Error(
        val code: Int,
        val fbtrace_id: String,
        val message: String,
        val type: String
    )
}