package com.spaceo.myapplication.insdataselection.model

import com.google.gson.annotations.SerializedName

data class Cursors(

	@field:SerializedName("before")
	val before: String? = null,

	@field:SerializedName("after")
	val after: String? = null
)