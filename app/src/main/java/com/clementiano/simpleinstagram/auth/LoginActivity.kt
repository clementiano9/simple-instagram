package com.clementiano.simpleinstagram.auth

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.net.Uri
import android.util.Log
import android.webkit.WebViewClient
import com.afollestad.materialdialogs.MaterialDialog
import com.clementiano.simpleinstagram.R
import com.clementiano.simpleinstagram.data.PreferenceStore
import com.clementiano.simpleinstagram.main.MainActivity
import com.clementiano.simpleinstagram.network.ApiInterface
import com.clementiano.simpleinstagram.network.RetrofitClient
import kotlinx.android.synthetic.main.activity_login.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.webkit.CookieManager
import com.clementiano.simpleinstagram.BuildConfig


class LoginActivity : AppCompatActivity() {

    val TAG: String = LoginActivity::class.java.simpleName

    lateinit var redirectUrl: String
    lateinit var requestUrl: String
    lateinit var accessToken: String
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var preferenceStore: PreferenceStore

    private val  apiInterface: ApiInterface = RetrofitClient.getBasicApiInterface()
    private val graphApi = RetrofitClient.getGraphApiInterface()

    var accessGotten = false

    private val webViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url != null && url.startsWith(redirectUrl)) {
                extractAccessCode(url)
                return true
            }
            return false
        }

        override fun onPageFinished(view: WebView?, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.INVISIBLE
            extractAccessCode(url)
        }
    }

    private fun extractAccessCode(url: String) {
        if (url.contains("code=")) {
            accessToken = url.substring(url.indexOf("=") + 1, url.indexOf("#"))
            onTokenReceived(this@LoginActivity.accessToken)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferenceStore = PreferenceStore(this)

        if (preferenceStore.isLoggedIn) {
            launchMainScreen()

        } else {
            redirectUrl = resources.getString(R.string.redirect_url)
            requestUrl = resources.getString(R.string.base_url) +
                    "oauth/authorize/?client_id=" +
                    resources.getString(R.string.client_id) +
                    "&redirect_uri=" + redirectUrl +
                    "&response_type=code&display=touch&scope=user_profile,user_media"
            clientId = resources.getString(R.string.client_id)
            clientSecret = BuildConfig.CLIENT_SECRET

            // Invalidate session details to enable a fresh login
            val cookieString = "sessionid=''"
            val instance = CookieManager.getInstance()
            instance.setCookie("https://www.instagram.com", cookieString)

            webView.settings.javaScriptEnabled = true
            webView.webViewClient = webViewClient
            webView.loadUrl(requestUrl)
        }
    }

    private fun onTokenReceived(authCode: String) {
        if (!accessGotten) {
            apiInterface.getAccessToken(clientId, clientSecret, authCode, redirectUrl = redirectUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    progressBar.visibility = View.VISIBLE
                }
                .subscribe({
                    accessGotten = true
                    getLongLivedToken(it.access_token)

                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun getLongLivedToken(accessToken: String) {
        graphApi.getLongLivedAccessToken(clientSecret, accessToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                preferenceStore.setUserLoggedIn(it.access_token)
                launchMainScreen()
            }, {
                MaterialDialog(this).show {
                    title = "Login failed"
                    positiveButton(text = "Ok")
                }
                it.printStackTrace()
            })
    }

    private fun launchMainScreen() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
}
