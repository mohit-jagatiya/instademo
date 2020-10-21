package com.spaceo.myapplication.insdataselection.model

import com.google.gson.annotations.SerializedName

data class DataItem(

        @field:SerializedName("caption")
        val caption: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("media_url")
        val media_url: String? = null,

        @field:SerializedName("permalink")
        val permalink: String? = null,

        @field:SerializedName("media_type")
        val media_type: String? = null,

        @field:SerializedName("children")
        val children: Children? = null
)