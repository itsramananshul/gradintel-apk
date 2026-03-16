package com.gradintel.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Animate logo icon
        val logoIcon = findViewById<ImageView>(R.id.splash_logo_icon)
        val logoText = findViewById<TextView>(R.id.splash_logo_text)
        val logoSub  = findViewById<TextView>(R.id.splash_logo_sub)

        val popAnim   = AnimationUtils.loadAnimation(this, R.anim.pop_in)
        val fadeUpAnim = AnimationUtils.loadAnimation(this, R.anim.fade_up)

        logoIcon.startAnimation(popAnim)
        logoText.startAnimation(fadeUpAnim)
        logoSub.startAnimation(fadeUpAnim)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 1800)
    }
}
