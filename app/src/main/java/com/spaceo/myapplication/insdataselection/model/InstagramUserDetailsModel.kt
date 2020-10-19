package com.spaceo.myapplication.insdataselection.model

data class InstagramUserDetailsModel(
    val error: Error,
    val id: String,
    val username: String,
    val profile_picture : String
) {
    data class Error(
        val code: Int,
        val fbtrace_id: String,
        val message: String,
        val type: String
    )
}