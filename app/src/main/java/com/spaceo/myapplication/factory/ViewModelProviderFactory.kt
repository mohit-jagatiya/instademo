package com.spaceo.myapplication.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spaceo.myapplication.repository.LoginRepository
import com.spaceo.myapplication.viewmodels.MainViewModel
import kotlin.reflect.KClass

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class ViewModelProviderFactory(private val application: Application) : ViewModelProvider.Factory {

    fun <T : ViewModel, U : ViewModel> Class<T>.`is`(modelClass: KClass<U>): Boolean {
        return isAssignableFrom(modelClass.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(clazz: Class<T>): T {
        return when {
            clazz.`is`(MainViewModel::class) -> {
                MainViewModel(application = application, repository = LoginRepository()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

