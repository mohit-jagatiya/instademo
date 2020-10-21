package com.spaceo.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramlogin.ItemViewHolder
import com.example.instagramlogin.LoadingViewHolder
import com.spaceo.myapplication.R
import com.spaceo.myapplication.utils.VIEW_TYPE_ITEM
import com.spaceo.myapplication.utils.VIEW_TYPE_LOADING
import kotlinx.android.synthetic.main.item_gallery_image.view.*

class InstagramImageListAdapter(private val items: ArrayList<Any?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_image, parent, false))
        } else if (viewType == VIEW_TYPE_LOADING) {
            return LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false))
        } else {
            throw  RuntimeException("This is not type one or not two")
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (items.get(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ItemViewHolder){

            items as ArrayList<String>

            Glide.with(holder.itemView.context).load(items[position]).into(holder.itemView.itemImage)
        }

    }

}