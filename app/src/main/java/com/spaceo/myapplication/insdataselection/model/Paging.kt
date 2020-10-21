package com.spaceo.myapplication.insdataselection.model

import com.google.gson.annotations.SerializedName
import com.spaceo.myapplication.insdataselection.model.Cursors

data class Paging(

    @field:SerializedName("next")
	val next: String? = null,

    @field:SerializedName("cursors")
	val cursors: Cursors? = null,


    @field:SerializedName("previous")
	val previous: String? = null


)