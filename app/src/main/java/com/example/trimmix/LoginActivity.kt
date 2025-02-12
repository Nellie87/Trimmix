package com.example.trimmix

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException
import androidx.fragment.app.Fragment


class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectTextView: TextView
    private lateinit var navigateToFirstFragmentButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
// figure out how it's being used
//        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        signupRedirectTextView = findViewById(R.id.signupRedirect)
        navigateToFirstFragmentButton = findViewById(R.id.navigateToFirstFragmentButton) // Initialize button

        // Login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(email, password)
        }

        // Signup redirect listener
        signupRedirectTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // Navigate to FirstFragment button click listener
//        navigateToFirstFragmentButton.setOnClickListener {
//            replaceFragment(FirstFragment()) // Replaces with FirstFragment
//        }
        navigateToFirstFragmentButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Close LoginActivity so the user can't go back
        }

    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment) // Replaces the activity content with the fragment
            .addToBackStack(null)
            .commit()
    }

    private fun loginUser(email: String, password: String) {
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("http://your_api_url/login") // Replace with your API URL
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Handle successful login response
                } else {
                    // Handle error response
                }
            }
        })
    }
}