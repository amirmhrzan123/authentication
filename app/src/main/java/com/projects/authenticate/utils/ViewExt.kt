package com.projects.authenticate.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.projects.authenticate.R
import kotlin.math.floor


const val CHAT_WIDTH_PERCENTAGE = 0.70
const val CHAT_WIDTH_STICKER_PERCENTAGE = 0.50
const val CHAT_HEIGHT_PERCENTAGE = 0.80
const val CHAT_STORY_WIDTH_PERCENTAGE = 0.25


fun Context.showAlert(message: String?) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.Theme_Authenticate)
    builder.setTitle(R.string.app_name)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
}

fun Activity.showToast(message: String?) {
    Toast.makeText(this, message ?: "", Toast.LENGTH_SHORT).show()
}


fun Context.showNotCancellableAlert(message: String,
                                    action: () -> Unit = {}) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.Theme_Authenticate)
    builder.setTitle(R.string.app_name)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                action()
            }
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}

fun Context.showConfirmationDialog(message: String,
                                   positiveButton: String = "Ok",
                                   negativeButton: String = "Cancel",
                                   negativeAction: () -> Unit = {},
                                   positiveAction: () -> Unit
) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.Theme_Authenticate)
    builder.setTitle(R.string.app_name)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, _ ->
                dialog.dismiss()
                positiveAction()
            }
            .setNegativeButton(negativeButton) { dialog, _ ->
                dialog.dismiss()
                negativeAction()
            }
            .show()
}

fun Context.showPermissionRequestDialog(message: Int) {
    showConfirmationDialog(
            message = getString(message),
            positiveButton = "Settings",
            negativeButton = "Not Now",
            positiveAction = {
                val i = Intent()
                i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                i.addCategory(Intent.CATEGORY_DEFAULT)
                i.data = Uri.parse("package:$packageName")
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(i)
            })
}

fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.showToast(message: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun TextView.fromHtml(html: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(html)
    }
}

fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

inline fun EditText.onFocusChange(crossinline hasFocus: (Boolean) -> Unit) {
    onFocusChangeListener = View.OnFocusChangeListener { view, b -> hasFocus(b)}
}

/**
 * Implements [TextWatcher] and sends only the result of [TextWatcher.afterTextChanged]
 */
fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)



/**
 * @return max width of each individual chat item which is equivalent to [CHAT_WIDTH_PERCENTAGE] of screen size.
 */
fun Context.getChatItemWidth(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    return (floor(CHAT_WIDTH_PERCENTAGE * displayMetrics.widthPixels)).toInt()
}


/**
 * Method to set max width of chat item
 * Max Width will be [CHAT_WIDTH_PERCENTAGE] of screen size
 */
fun TextView.setChatItemMaxWidth(chatItemWidth: Int = 0) {
    val percentageWidth = if (chatItemWidth == 0) {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        (floor(CHAT_WIDTH_PERCENTAGE * displayMetrics.widthPixels)).toInt()
    } else {
        chatItemWidth
    }
    maxWidth = percentageWidth
}

/**
 * Method to set minimum width of chat item
 * Width will be [CHAT_WIDTH_PERCENTAGE] of screen size
 */
fun TextView.setChatItemWidth(chatItemWidth: Int = 0) {
    val percentageWidth = if (chatItemWidth == 0) {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        (floor(CHAT_WIDTH_PERCENTAGE * displayMetrics.widthPixels)).toInt()
    } else {
        chatItemWidth
    }
    requestLayout()
    layoutParams.width = percentageWidth//In px
}




val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()