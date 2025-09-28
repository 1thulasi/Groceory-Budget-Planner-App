package com.example.smartgrocery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.model.GroceryItem

class GrocerySectionAdapter(
    private val context: Context,
    private var groceryMap: Map<String, List<GroceryItem>>
) : RecyclerView.Adapter<GrocerySectionAdapter.CategoryViewHolder>() {

    private var categories = groceryMap.keys.toList()

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textCategory: TextView = view.findViewById(R.id.textCategory)
        val recyclerItems: RecyclerView = view.findViewById(R.id.recyclerItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_grocery_section, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val items = groceryMap[category] ?: emptyList()

        holder.textCategory.text = category
        holder.recyclerItems.layoutManager = LinearLayoutManager(context)
        holder.recyclerItems.adapter = ManageGroceryAdapter(context, items.toMutableList())
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newMap: Map<String, List<GroceryItem>>) {
        groceryMap = newMap
        categories = newMap.keys.toList()
        notifyDataSetChanged()
    }
}
