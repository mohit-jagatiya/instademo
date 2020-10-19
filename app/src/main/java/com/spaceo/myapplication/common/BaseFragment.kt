/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 https://www.spaceotechnologies.com
 *
 * Permissions is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.spaceo.myapplication.common

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.spaceo.myapplication.utils.COMMON_REQ_CODE
import com.spaceo.myapplication.factory.ViewModelProviderFactory
import kotlin.reflect.KClass

abstract class BaseFragment<V : BaseViewModel, B : ViewDataBinding> : Fragment() {


    protected lateinit var viewModel: V
    protected abstract val modelClass: KClass<V>
    protected abstract val layoutId: Int
    protected lateinit var binding: B
    protected lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProviderFactory(requireActivity().application)).get(modelClass.java)
//        prefs = requireContext().P.customPrefs(this)
        binding.lifecycleOwner = this
        initControls()
    }

    protected abstract fun initControls()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        viewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun startActivityForResult(intent: Intent, callback: (data: Intent?) -> Unit) {
        startActivityForResult(intent) { resultCode, data ->
            if (resultCode == Activity.RESULT_OK) {
                callback(data)
            }
        }
    }

    private lateinit var resultCallback: (resultCode: Int, data: Intent?) -> Unit
    private fun startActivityForResult(intent: Intent, callback: (resultCode: Int, data: Intent?) -> Unit) {
        resultCallback = callback
        startActivityForResult(intent, COMMON_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (::resultCallback.isInitialized && requestCode == COMMON_REQ_CODE)
            resultCallback(resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
