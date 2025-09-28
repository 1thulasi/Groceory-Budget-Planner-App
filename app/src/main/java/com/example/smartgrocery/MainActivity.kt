package com.example.smartgrocery

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnUserLogin = findViewById<Button>(R.id.btnUserLogin)
        val btnAdminLogin = findViewById<Button>(R.id.btnAdminLogin)

        btnUserLogin.setOnClickListener {
            startActivity(Intent(this, UserLoginActivity::class.java))
        }

        btnAdminLogin.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }
    }
}
