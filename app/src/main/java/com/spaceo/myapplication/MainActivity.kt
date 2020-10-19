package com.spaceo.myapplication

import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.spaceo.myapplication.insdataselection.CustomDialogClass
import com.spaceo.myapplication.insdataselection.model.InstagramAccesTokenModel
import com.spaceo.myapplication.common.BaseActivity
import com.spaceo.myapplication.databinding.ActivityMainBinding
import com.spaceo.myapplication.utils.AUTHORIZATION_CODE
import com.spaceo.myapplication.viewmodels.MainViewModel
import com.spaceo.myapplication.factory.Resource
import com.spaceo.myapplication.insdataselection.model.InstagramUserDetailsModel
import kotlin.reflect.KClass

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() , CustomDialogClass.CallBackUrlResponseListener {

    override val layoutId = R.layout.activity_main
    override val modelClass: KClass<MainViewModel> = MainViewModel::class
    private lateinit var customDialogClass: CustomDialogClass

    override fun initControls() {
        addObserver()
        binding.btnLogin.setOnClickListener {
            customDialogClass = CustomDialogClass(this, this)
            customDialogClass.show()
            customDialogClass.window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        }
    }
    private fun addObserver() {
        viewModel.status.observe(this, mObserver)
    }

    private val mObserver = Observer<Any> {
        when (it) {

            is Resource.Error<*> -> {
                Log.e("error", it.toString())
            }

            is Resource.Success<*> -> {
                if (it.data is InstagramAccesTokenModel)
                     viewModel.getData(it.data)
                else if (it.data is InstagramUserDetailsModel) {
                    Log.e("data",it.data.toString())
                     val data = it.data
                    binding.info.text = "id  ${data.id} name  ${data.username}"
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
    }

}

