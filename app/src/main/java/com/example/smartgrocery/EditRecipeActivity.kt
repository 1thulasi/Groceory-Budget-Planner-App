package com.example.smartgrocery.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartgrocery.R
import com.google.firebase.database.FirebaseDatabase

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtDesc: EditText
    private lateinit var btnSave: Button
    private var recipeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        edtName = findViewById(R.id.editRecipeName)
        edtDesc = findViewById(R.id.editRecipeDescription)
        btnSave = findViewById(R.id.btnSaveRecipe)

        recipeId = intent.getStringExtra("recipeId")
        if (recipeId == null) {
            Toast.makeText(this, "Invalid recipe", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadRecipeData()

        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()
            val desc = edtDesc.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedData = mapOf(
                "name" to name,
                "description" to desc
            )

            FirebaseDatabase.getInstance().getReference("Recipes")
                .child(recipeId!!)
                .updateChildren(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Recipe updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadRecipeData() {
        val ref = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId!!)
        ref.get().addOnSuccessListener {
            edtName.setText(it.child("name").value.toString())
            edtDesc.setText(it.child("description").value.toString())
        }
    }
}
