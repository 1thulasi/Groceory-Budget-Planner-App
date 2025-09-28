package com.example.smartgrocery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartgrocery.admin.AdminDashboardActivity
import com.google.firebase.auth.FirebaseAuth


class AdminLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var adminEmailEditText: EditText
    private lateinit var adminPasswordEditText: EditText
    private lateinit var adminLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        auth = FirebaseAuth.getInstance()

        adminEmailEditText = findViewById(R.id.editTextAdminEmail)
        adminPasswordEditText = findViewById(R.id.editTextAdminPassword)
        adminLoginButton = findViewById(R.id.buttonAdminLogin)

        adminLoginButton.setOnClickListener {
            val email = adminEmailEditText.text.toString()
            val password = adminPasswordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
