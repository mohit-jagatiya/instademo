package com.spaceo.myapplication.insdataselection.model

import com.google.gson.annotations.SerializedName

data class MediaResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("paging")
	val paging: Paging? = null
)