package com.example.smartgrocery.admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.google.firebase.database.*
import com.example.smartgrocery.admin.Recipe

class   ManageRecipeActivity : AppCompatActivity() {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var searchBox: EditText
    private lateinit var txtNoRecipes: TextView
    private lateinit var recipeAdapter: ManageRecipeAdapter
    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_recipe)

        Toast.makeText(this, "ManageRecipeActivity opened", Toast.LENGTH_SHORT).show()

        searchBox = findViewById(R.id.searchRecipe)
        txtNoRecipes = findViewById(R.id.txtNoRecipes)
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView)
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)

        recipeList = mutableListOf()
        recipeAdapter = ManageRecipeAdapter(this, recipeList)
        recipeRecyclerView.adapter = recipeAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("Recipes")

        loadRecipes()

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                recipeAdapter.filter.filter(query)
            }
        })
    }

    private fun loadRecipes() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeList.clear()
                for (recipeSnapshot in snapshot.children) {
                    val recipe = recipeSnapshot.getValue(Recipe::class.java)
                    recipe?.recipeId = recipeSnapshot.key ?: ""
                    recipeList.add(recipe!!)
                }
                recipeAdapter.notifyDataSetChanged()

                // Show or hide "No Recipes Found"
                txtNoRecipes.visibility = if (recipeList.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageRecipeActivity, "Failed to load recipes", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
