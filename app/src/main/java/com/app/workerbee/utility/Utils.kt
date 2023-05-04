package com.app.workerbee.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.app.workerbee.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun checkAccessToken(mContext: Context) {
        /*user authentication token*/
        if (Constant.authentication_header_token.isEmpty()) {
            Constant.authentication_header_token =
                MyPreferences(mContext).getString(Constant.API_TOKEN)
        }
        Log.d("api Token: ", Constant.authentication_header_token)
    }

    fun isConnectedToInternet(context: Context): Boolean {
        var result = false // Returns connection type. 0: none; 1: mobile data; 2: wifi; 3: vpn
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = true
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = true
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = true
                }
            }
        }
        return result
    }

    fun isEditTextEmpty(editText: EditText, errorMessage: String): Boolean {
        return if (editText.text.toString().isBlank()) {
            editText.error = errorMessage
            editText.requestFocus()
            true
        } else {
            false
        }
    }

    fun isTextEmpty(text: String, errorMessage: String, context: Context): Boolean {
        return if (text.isNullOrEmpty()) {
            showToast(context, errorMessage)
            true
        } else {
            false
        }
    }

    fun isEditTextEmptyDisplayToast(
        editText: EditText,
        errorMessage: String,
        mContext: Context
    ): Boolean {
        return if (editText.text.toString().isBlank()) {
            showToast(mContext, errorMessage)
            true
        } else {
            false
        }
    }

    fun isPasswordConfirmPasswordSame(
        password: EditText,
        confirmPassword: EditText,
        errorMessage: String
    ): Boolean {
        if (password.text.toString() != confirmPassword.text.toString()) {
            confirmPassword.error = errorMessage
            confirmPassword.requestFocus()
            return true
        }
        return false
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }



    fun isNotValidPassword(password: EditText, mContext: Context): Boolean {
        return if (password.text.toString().length < 6) {
            showToast(mContext, "Use 6 characters or more for your password")
            true
        } else {
            false
        }
    }

    fun isRadioButtonNotSelected(
        radioGroup: RadioGroup,
        errorMessage: String,
        mContext: Context
    ): Boolean {
        return if (radioGroup.checkedRadioButtonId == -1) {
            // no radio buttons are checked
            showToast(mContext, errorMessage)
            true
        } else {
            false
        }
    }

    fun setEditTextError(editText: EditText, errorMessage: String) {
        editText.error = errorMessage
        editText.requestFocus()
    }

    fun getText(editText: EditText?): String {
        return editText?.text.toString().trim()
    }

    fun getJsonObject(responseBody: ResponseBody): JSONObject {
        return JSONObject(responseBody.string())
    }

    fun getJsonObject(responseBody: String): JSONObject {
        return JSONObject(responseBody)
    }

    fun isSuccessResponse(jsonObject: JSONObject): Boolean {
        return jsonObject.optString("status").equals("1", true)
    }

    fun getResponseMessage(jsonObject: JSONObject): String {
        return jsonObject.optString("message", "")
    }

    fun getResponseMessage(jsonObject: JsonObject): String {
        return jsonObject.get("message").asString
    }

    fun getDataObject(jsonObject: JSONObject): JSONObject {
        return jsonObject.getJSONObject("data")
    }

    fun getDataArrayObject(jsonObject: JSONObject): JSONArray {
        return jsonObject.getJSONArray("data")
    }

    fun loadImage(mContext: Context, url: String, imageView: ImageView) {
        val requestOptions =
            RequestOptions.placeholderOf(R.drawable.icon_place_holder).error(R.drawable.icon_place_holder)//.override(200, 200)

        Glide.with(mContext)
            .load(url.replace(" ", ""))
            .apply(requestOptions)
            .into(imageView)
    }


    fun showToast(mContext: Context, message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    fun getErrorMessage(mContext: Context, throwable: Throwable): String {
        return throwable.message.takeIf { throwable.message != null } ?: ""
    }

    fun replaceNull(input: String?): String {
        return if (input == null || input == "null") {
            ""
        } else input ?: ""
    }

    fun getVideoOutputFile(mContext: Context): File {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
        val fileName = formatter.format(Calendar.getInstance().timeInMillis)
        val mFile = File(
            Environment
                .getExternalStoragePublicDirectory(mContext.getString(R.string.app_name))
                .toString() + "/Videos"
        )
        mFile.mkdirs()

        val fullFileName = mFile.absolutePath + "/$fileName.mp4"
        return File(fullFileName)
    }

    fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var image = image
        if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > 1) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            return image
        } else {
            return image
        }
    }


    fun createPartFromString(value: String): RequestBody {
        // return RequestBody.create(MediaType.parse("text/plain") , value)
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    fun loadFile(mContext: Context, file: File, imageView: ImageView) {
        val requestOptions =
            RequestOptions.skipMemoryCacheOf(true).signature { file.lastModified() }.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)

        Glide.with(mContext)
            .load(file)
            .apply(requestOptions)
            .into(imageView)
    }


    fun loadFileUri(mContext: Context, uri: Uri, imageView: ImageView) {

        Glide.with(mContext)
            .load(uri)
            .into(imageView)

    }

    fun createMultiPartFile(mContext: Context, key: String, file: File?): MultipartBody.Part {

        if (file == null) {
            val attachmentEmpty = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            return MultipartBody.Part.createFormData(key, "", attachmentEmpty)
        }

        val fileURI = FileProvider.getUriForFile(mContext, mContext.packageName + ".provider", file)
        val requestFile = RequestBody.create(mContext.contentResolver.getType(fileURI)?.let {
            it
                .toMediaTypeOrNull()
        }, file)

        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }


    fun createRequestBody(value: Any): RequestBody {
        val stringValue: String = if (value is String) {
            value
        } else {
            value.toString()
        }

        return stringValue
            .toRequestBody(okhttp3.MultipartBody.FORM)
    }

    fun handleErrorMessage(errorBodyResponse: ResponseBody?, context: Context?) {
        val error = StringBuilder()
        try {
            if (errorBodyResponse == null) {
                error("Assertion failed")
            }
            val ereader = BufferedReader(
                InputStreamReader(
                    errorBodyResponse.byteStream()
                )
            )
            var eline: String? = null
            while (ereader.readLine().also { eline = it } != null) {
                error.append(eline)
            }
            ereader.close()
        } catch (e: java.lang.Exception) {
            error.append(e.message)
        }

        try {
            val reader = JSONObject(error.toString())
            val message = reader.getString("message")
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    //Invite Friend (Like Share link)
    fun shareImageWithText(activity: Activity) {
        val link = "https://play.google.com/store/apps/details?id=com.app.santabuddiesparent"

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, link)
        sendIntent.type = "text/plain"
        activity.startActivity(sendIntent)
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //------------- Null Checking Methods --------------

    fun checkNull(input: Any?): String {
        return input?.toString() ?: return "NA"
    }

    fun checkNull(input: String?): String {
        return when (input) {
            null -> "NA"
            "" -> "-"
            else -> input
        }
    }

    fun checkNull(input: String?, show: String): String {
        return when (input) {
            null -> show
            " " -> show
            "NA" -> show
            else -> input
        }
    }

    fun checkNull(input: Int?): Int {
        return input ?: 0
    }

    fun checkNull(input: Long?): Long {
        return input ?: 0
    }

    fun checkNull(input: Float?): Float {
        return input ?: 0f
    }

    fun checkNull(input: Double?): Double {
        return input ?: 0.0
    }

    fun checkNull(input: Boolean?): Boolean {
        return input == true
    }

    //------------------- Get Double ------------------

    fun getDouble(doubleValue: Double): String {
        return String.format(Locale.US, "%.0f", doubleValue)
    }


    //------------------- Round Image ----------------

    fun loadRoundImage(mContext: Context, URL: String, imageView: ImageView) {

        val requestOptions = RequestOptions.placeholderOf(R.drawable.icon_logo)
            .error(R.drawable.icon_logo)

        Glide.with(mContext)
            .load(URL)
            .apply(requestOptions.circleCrop())
            .into(imageView)
    }

    //------------------- File Image ----------------

    fun loadFileImage(mContext: Context, file: File, imageView: ImageView) {

        val requestOptions =
            RequestOptions.skipMemoryCacheOf(true).signature { file.lastModified() }.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)

        Glide.with(mContext)
            .load(file)
            .apply(requestOptions)
            .into(imageView)
    }

}