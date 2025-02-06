package com.example.trimmix

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsControllerCompat
import android.content.SharedPreferences

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view
        setContentView(R.layout.activity_splash)

        // Enable edge-to-edge support for the splash screen
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true // To set the status bar icons to light if needed
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars()) // Hide system bars for full-screen splash
        }

        // Check login status and navigate accordingly
        Handler().postDelayed({
            navigateToNextScreen()
        }, 2000) // 2 seconds delay
    }

    // Method to navigate to the login screen or main screen based on login status
    private fun navigateToNextScreen() {
        // Check login status from SharedPreferences or session
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        val intent = if (isLoggedIn) {
            Intent(this, MainActivity::class.java) // User is logged in, navigate to MainActivity
        } else {
            Intent(this, LoginActivity::class.java) // User is not logged in, navigate to LoginActivity
        }

        startActivity(intent)
        finish() // Close SplashActivity so the user can't navigate back to it
    }
}