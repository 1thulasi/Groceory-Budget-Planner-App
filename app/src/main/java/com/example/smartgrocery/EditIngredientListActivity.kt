package com.example.smartgrocery.user

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.smartgrocery.user.Ingredient

class EditIngredientListActivity : AppCompatActivity() {

    private lateinit var ingredientAdapter: EditIngredientAdapter
    private lateinit var ingredientList: MutableList<Ingredient>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ingredient_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEditIngredients)
        val btnAddToGroceryList = findViewById<Button>(R.id.btnAddToGroceryList)

        ingredientList = intent.getSerializableExtra("ingredientList") as? MutableList<Ingredient> ?: mutableListOf()

        ingredientAdapter = EditIngredientAdapter(ingredientList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ingredientAdapter

        btnAddToGroceryList.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val ref = FirebaseDatabase.getInstance().getReference("UserGroceryList").child(userId)

            for (ingredient in ingredientList) {
                val key = ref.push().key
                if (key != null) {
                    ref.child(key).setValue(ingredient)
                }
            }

            Toast.makeText(this, "Ingredients added to your grocery list!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
