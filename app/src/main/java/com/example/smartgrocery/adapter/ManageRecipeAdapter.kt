package com.example.smartgrocery.admin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ManageRecipeAdapter(private val context: Context, private val recipes: MutableList<Recipe>) :
    RecyclerView.Adapter<ManageRecipeAdapter.ViewHolder>(), Filterable {

    private var filteredList = recipes.toMutableList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.recipeName)
        val txtDesc: TextView = itemView.findViewById(R.id.recipeDescription)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditRecipe)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteRecipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recipe_manage, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = filteredList[position]
        holder.txtName.text = recipe.name
        holder.txtDesc.text = recipe.description

        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditRecipeActivity::class.java)
            intent.putExtra("recipeId", recipe.recipeId)
            context.startActivity(intent)
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Yes") { _, _ ->
                    val recipeId = recipe.recipeId ?: return@setPositiveButton

                    FirebaseDatabase.getInstance().getReference("Recipes")
                        .child(recipeId)
                        .removeValue()
                        .addOnSuccessListener {
                            // Remove from original and filtered list
                            recipes.removeIf { it.recipeId == recipeId }
                            val adapterPosition = holder.adapterPosition
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                filteredList.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                            }
                            Toast.makeText(context, "Recipe deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to delete recipe", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val search = query.toString().lowercase(Locale.ROOT)
                filteredList = if (search.isEmpty()) {
                    recipes.toMutableList()
                } else {
                    recipes.filter {
                        it.name?.lowercase(Locale.ROOT)?.contains(search) == true
                    }.toMutableList()
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(query: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<Recipe>
                notifyDataSetChanged()
            }
        }
    }
}
