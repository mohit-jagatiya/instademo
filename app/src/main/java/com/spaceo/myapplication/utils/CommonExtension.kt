package com.spaceo.myapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.widget.AppCompatEditText

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData


/**
 * Return trimmed text of EditText
 * */
fun EditText.getTrimText(): String = text.toString().trim()

/**
 * Return true If EditText is empty otherwise false
 * */
fun EditText.isEmpty(): Boolean = TextUtils.isEmpty(text.toString().trim())

fun View.isVisible() = visibility == View.VISIBLE

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.color(resource: Int) = ContextCompat.getColor(this, resource)
fun Activity.color(resource: Int) = ContextCompat.getColor(this, resource)
fun Fragment.color(resource: Int) = ContextCompat.getColor(this.requireContext(), resource)

fun Context.colorStateList(resource: Int) = ContextCompat.getColor(this, resource)
fun Activity.colorStateList(resource: Int) = ContextCompat.getColor(this, resource)
fun Fragment.colorStateList(resource: Int) = ContextCompat.getColor(this.requireContext(), resource)

fun AppCompatTextView.setRequired() {
    val simple = text.toString()
    val colored = " *"
    val builder = SpannableStringBuilder(simple + colored)
    builder.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF5D5D")),
            simple.length,
            builder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = builder
}

fun Int.isSuccess() = this == SUCCESS

fun EditText.onTextChange(callback: (s: CharSequence?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            callback(s)
        }
    })
}


fun LiveData<Boolean>.isTrue() = this.value ?: false
fun LiveData<Boolean>.isFalse() = !isTrue()

fun Context.isNightMode() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

fun Context.layoutInflater() = (this as Activity).layoutInflater

fun AppCompatEditText.hidePassword(isHide: Boolean) {
    transformationMethod =
            if (isHide) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
    setSelection(length())
}
/**
 * Kotlin Extensions for simpler, easier and funw way
 * of launching of Activities
 */

inline fun <reified T : Any> Activity.launchActivity (
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
    {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

inline fun <reified T : Any> Context.launchActivity (
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
    {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)
