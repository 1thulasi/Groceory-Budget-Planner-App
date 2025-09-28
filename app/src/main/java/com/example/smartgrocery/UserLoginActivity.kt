package com.example.smartgrocery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartgrocery.admin.AdminDashboardActivity
import com.example.smartgrocery.user.UserDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class UserLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonUserLogin)
        signupButton = findViewById(R.id.buttonGoToSignup)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser!!.uid

                            // Check if user data exists in Realtime Database
                            database.child(userId).get().addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    val role = snapshot.child("role").value as? String
                                    if (role == "admin") {
                                        Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                                    } else {
                                        Toast.makeText(this, "User Login Successful", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, UserDashboardActivity::class.java))
                                    }
                                    finish()
                                } else {
                                    Toast.makeText(this, "User data not found. Please sign up first.", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to Signup Page
        signupButton.setOnClickListener {
            val intent = Intent(this, UserSignupActivity::class.java)
            startActivity(intent)
        }
    }
}
