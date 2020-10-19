package com.spaceo.myapplication.viewmodels

import android.app.Application
import com.spaceo.myapplication.repository.LoginRepository
import com.spaceo.myapplication.common.BaseViewModel
import com.spaceo.myapplication.insdataselection.model.InstagramAccesTokenModel
import com.spaceo.myapplication.insdataselection.model.InstagramUserDetailsModel


class MainViewModel(application: Application, val repository: LoginRepository) : BaseViewModel(application) {

    private val mContext = application.applicationContext

    fun login(appID: String, appSecret: String, grantType: String, redirectUri: String, code: String) =
        status.addSource(repository.getInstaramUserIDAndAccessToken(  appID, appSecret, grantType, redirectUri, code)) {
        status.value = it
    }
     fun getData(data: InstagramAccesTokenModel) = status.addSource(repository.getUserDetailsFromInstagram(data.user_id.toString(),data.access_token)){
         status.value = it
     }


}
