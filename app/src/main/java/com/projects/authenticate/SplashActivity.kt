package com.projects.authenticate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.projects.authenticate.facerecognition.FaceDetectionActivity

class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val intent = Intent(this,AuthenticateActivity::class.java)
            startActivity(intent)
            finishAffinity()

        },500)
    }
}