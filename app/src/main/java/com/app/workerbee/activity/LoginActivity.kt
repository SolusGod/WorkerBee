package com.app.workerbee.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.app.workerbee.R
import com.app.workerbee.activity.base.BaseActivity
import com.app.workerbee.databinding.ActivityLoginBinding
import com.app.workerbee.utility.Constant
import com.app.workerbee.utility.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : BaseActivity() {

    private var start = true
    private lateinit var handler: Handler

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            updateUI()
        }, 1500)

        /*        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestScopes(Scope("https://www.googleapis.com/auth/youtube.force-ssl"))
                    .build()

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

                binding.signInButton.setOnClickListener {

                    val signInIntent = mGoogleSignInClient!!.signInIntent
                    startActivityForResult(signInIntent, signInRequestCode)*/
    }

    private fun updateUI() {
        if (myPreferences!!.getBoolean(Constant.IS_NEW_USER)) {
            startActivity(Intent(this, ChannelActivity::class.java))
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        if (!start) {
            handler.postDelayed({
                updateUI()
            }, 2000)
        }
    }

    override fun onPause() {
        super.onPause()
        start = false
        handler.removeCallbacksAndMessages(null)
    }

}