package com.app.workerbee.activity.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.workerbee.R
import com.app.workerbee.utility.MyPreferences
import io.reactivex.disposables.Disposable
import org.bouncycastle.jce.provider.BouncyCastleProvider

import java.security.Security

abstract class BaseActivity : AppCompatActivity()  {

    var disposable: Disposable? = null

    val mContext: Context
    get() = this

    var myPreferences: MyPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Security.addProvider(BouncyCastleProvider())
        myPreferences = MyPreferences(mContext)
    }

    override fun onDestroy() {
        //We dispose here, so that it will not get update after activity is destroyed.
        disposable?.dispose()
        super.onDestroy()
    }

    open fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showError(message: String) {
        //AlertMessage.showMessage(mContext, message)
    }

    private fun setStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(mContext, R.color.theme)
    }

    open fun showLog(tag: String, message: String){
        Log.d(tag, message)
    }

}
