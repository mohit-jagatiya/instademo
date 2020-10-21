package com.spaceo.myapplication.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spaceo.myapplication.R
import com.spaceo.myapplication.adapter.InstagramImageListAdapter
import com.spaceo.myapplication.common.BaseActivity
import com.spaceo.myapplication.databinding.ActivityMainBinding
import com.spaceo.myapplication.factory.Resource
import com.spaceo.myapplication.insdataselection.CustomDialogClass
import com.spaceo.myapplication.insdataselection.model.InstagramAccesTokenModel
import com.spaceo.myapplication.insdataselection.model.InstagramUserDetailsModel
import com.spaceo.myapplication.insdataselection.model.MediaResponse
import com.spaceo.myapplication.insdataselection.model.Paging
import com.spaceo.myapplication.utils.*
import com.spaceo.myapplication.viewmodels.MainViewModel
import kotlin.reflect.KClass


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() , CustomDialogClass.CallBackUrlResponseListener {

    private var accessTokenModal: InstagramAccesTokenModel? = null
    override val layoutId = R.layout.activity_main
    override val modelClass: KClass<MainViewModel> = MainViewModel::class
    private lateinit var customDialogClass: CustomDialogClass

    private var isCompleteLoading = false
    private var isLodingStarted = false
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var visibleItemCount = 0
    private var paging: Paging? = null
    private var imageList = ArrayList<Any?>()


    override fun initControls() {
        addObserver()
        binding.btnLogin.setOnClickListener {
            customDialogClass = CustomDialogClass(this, this)
            customDialogClass.show()
            customDialogClass.window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        binding.rvImages.apply {

            layoutManager = GridLayoutManager(this@MainActivity, 2)

            adapter = InstagramImageListAdapter(imageList)

            (layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (this@apply.adapter!!.getItemViewType(position)) {
                            VIEW_TYPE_ITEM -> 1
                            VIEW_TYPE_LOADING -> 2 //number of columns of the grid
                            else -> -1
                        }
                    }
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    if (dy > 0) {
                        totalItemCount = recyclerView.layoutManager!!.itemCount
                        lastVisibleItem = (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                        visibleItemCount = recyclerView.layoutManager!!.childCount
                        if (!isCompleteLoading && !isLodingStarted && totalItemCount <= lastVisibleItem + visibleItemCount) {
                            isLodingStarted = true

                            showLoading()

                            viewModel.getMedia(accessTokenModal!!, paging?.cursors?.after)

                        }
                    }
                }
            })
        }
    }

    private fun addObserver() {
        viewModel.status.observe(this, mObserver)
    }

    @SuppressLint("SetTextI18n")
    private val mObserver = Observer<Any> {
        when (it) {

            is Resource.Error<*> -> {
                Log.e("error", it.toString())
            }

            is Resource.Success<*> -> {
                when (it.data) {
                    is InstagramAccesTokenModel -> {
                        viewModel.run { getData(it.data) }
                        accessTokenModal = it.data
                    }
                    is InstagramUserDetailsModel -> {
                        paging = Paging()
                        viewModel.getMedia(accessTokenModal!!, paging?.cursors?.after)
                        binding.info.text = "Wellcome ${it.data.username}.."

                    }
                    is MediaResponse -> {
                        if (imageList.contains(null))
                            removeLoading()
                        binding.rvImages.makeVisible()
                        binding.btnLogin.gone()
                        hideProgressDialog()

                        paging = it.data.paging

                        it.data.data!!.forEach {data->
                            if (data!!.media_type.equals("IMAGE"))
                                imageList.add(data.media_url!!)
                            else if (data.media_type.equals("CAROUSEL_ALBUM")) {

                                data.children?.data?.forEach {
                                    imageList.add(data.media_url!!)
                                }
                            }
                        }




                        if (paging?.next.isNullOrEmpty()) {
                            isCompleteLoading = true
                        }

                        binding.rvImages.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }



    override fun gettingResponseCallBack(response: String) {
        Log.d("TAG", "instaresponse$response")
        customDialogClass.dismiss()
        val appID = getString(R.string.instagram_app_id)
        val appSecret = getString(R.string.instagram_app_secret)
        val grantType = AUTHORIZATION_CODE
        val redirectUrl = getString(R.string.instagram_redirect_url)

        viewModel.login(appID, appSecret, grantType, redirectUrl, response)
        showProgressDialog(this)
    }

    private fun showLoading() {

        if (imageList.size > 0) {
            imageList.add(null)
            binding.rvImages.adapter!!.notifyItemInserted(imageList.size - 1)

        }
    }

    private fun removeLoading() {
        if (imageList.size > 1) {
            imageList.removeAt(imageList.size - 1)
            binding.rvImages.adapter?.notifyItemRemoved(imageList.size)
        }
    }

}

