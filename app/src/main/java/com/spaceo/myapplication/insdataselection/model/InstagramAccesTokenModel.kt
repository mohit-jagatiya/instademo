package com.spaceo.myapplication.insdataselection.model

data class InstagramAccesTokenModel(
    val access_token: String,
    val code: Int,
    val error_message: String,
    val error_type: String,
    val user_id: Long
)