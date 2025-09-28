package com.example.smartgrocery.user

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.admin.Recipe
import com.google.firebase.database.*

class UserRecipeActivity : AppCompatActivity() {

    private lateinit var recipeAdapter: UserRecipeAdapter
    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_recipe)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerUserRecipes)
        val searchEditText = findViewById<EditText>(R.id.editSearchRecipe)

        recipeList = mutableListOf()
        recipeAdapter = UserRecipeAdapter(recipeList) { recipe ->
            // Convert to RecipeModel and map ingredients
            val ingredients = recipe.ingredients?.mapNotNull { item ->
                (item as? Map<*, *>)?.let { map ->
                    val name = map["name"] as? String ?: return@let null
                    val quantity = map["quantity"] as? String ?: ""
                    val unit = map["unit"] as? String ?: ""
                    Ingredient(name, quantity, unit)
                }
            } ?: emptyList()

            val recipeModel = RecipeModel(
                recipeId = recipe.recipeId ?: "",
                recipeName = recipe.name ?: "",
                description = recipe.description ?: "",
                ingredients = ingredients
            )

            val intent = Intent(this, UserRecipeDetailActivity::class.java)
            intent.putExtra("recipe", recipeModel)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter

        // Firebase load
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeList.clear()
                for (recipeSnap in snapshot.children) {
                    val map = recipeSnap.value as? Map<*, *> ?: continue

                    val name = map["name"] as? String ?: ""
                    val description = map["description"] as? String ?: ""
                    val ingredientsList = map["ingredients"] as? List<*> ?: emptyList<Any>()

                    val ingredientList = ingredientsList.mapNotNull { item ->
                        val ingredientMap = item as? Map<*, *> ?: return@mapNotNull null
                        val ingName = ingredientMap["name"] as? String ?: return@mapNotNull null
                        val quantity = ingredientMap["quantity"] as? String ?: ""
                        val unit = ingredientMap["unit"] as? String ?: ""
                        mapOf("name" to ingName, "quantity" to quantity, "unit" to unit)
                    }

                    val recipe = Recipe(
                        recipeId = recipeSnap.key ?: "",
                        name = name,
                        description = description,
                        ingredients = ingredientList
                    )

                    recipeList.add(recipe)
                }
                recipeAdapter.updateList(recipeList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserRecipeActivity, "Error loading recipes", Toast.LENGTH_SHORT).show()
            }
        })

        // Search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = recipeList.filter {
                    it.name?.contains(s.toString(), ignoreCase = true) == true
                }
                recipeAdapter.updateList(filtered)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
