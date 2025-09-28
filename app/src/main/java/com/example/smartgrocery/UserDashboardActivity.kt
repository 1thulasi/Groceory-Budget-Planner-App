package com.example.smartgrocery.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smartgrocery.R
import com.example.smartgrocery.UserLoginActivity

class UserDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val txtWelcome: TextView = findViewById(R.id.txtWelcome)
        val btnSetBudget: Button = findViewById(R.id.btnSetBudget)
        val btnSelectGroceries: Button = findViewById(R.id.btnSelectGroceries)
        val btnViewSelectedGroceries: Button = findViewById(R.id.btnViewSelectedGroceries)
        val btnBrowseRecipes: Button = findViewById(R.id.btnBrowseRecipes) // NEW
        val btnLogout: Button = findViewById(R.id.btnLogout)

        btnSetBudget.setOnClickListener {
            startActivity(Intent(this, SetBudgetActivity::class.java))
        }

        btnSelectGroceries.setOnClickListener {
            startActivity(Intent(this, UserGroceryListActivity::class.java))
        }

        btnViewSelectedGroceries.setOnClickListener {
            startActivity(Intent(this, ViewSelectedGroceriesActivity::class.java))
        }

        btnBrowseRecipes.setOnClickListener {
            startActivity(Intent(this, UserRecipeActivity::class.java)) // This will be created next
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
