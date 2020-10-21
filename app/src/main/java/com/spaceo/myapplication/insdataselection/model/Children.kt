package com.spaceo.myapplication.insdataselection.model

import com.google.gson.annotations.SerializedName
import com.spaceo.myapplication.insdataselection.model.DataItem

data class Children(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null
)