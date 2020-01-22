package com.clementiano.simpleinstagram.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.clementiano.simpleinstagram.R
import com.clementiano.simpleinstagram.data.PreferenceStore
import com.clementiano.simpleinstagram.main.MainActivity
import kotlinx.android.synthetic.main.activity_authentication.*

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val preferenceStore = PreferenceStore(this)

        if (preferenceStore.isLoggedIn) {
            launchMainScreen()
        }

        loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun launchMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}
