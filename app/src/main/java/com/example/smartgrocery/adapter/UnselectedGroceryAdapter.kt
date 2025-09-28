package com.example.smartgrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.model.GroceryItem

class UnselectedGroceryAdapter(private var groceries: MutableList<GroceryItem>) :
    RecyclerView.Adapter<UnselectedGroceryAdapter.GroceryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unselected_grocery, parent, false)
        return GroceryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val grocery = groceries[position]

        holder.groceryName.text = grocery.name
        holder.groceryPrice.text = "Price: ₹${grocery.price}"
        holder.groceryDiscount.text = "Discount: ₹${grocery.discount}"
        holder.groceryFinalPrice.text = "Final Price: ₹${grocery.getFinalPrice()}"

        // Set checkbox based on selection state
        holder.checkBox.setOnCheckedChangeListener(null) // Prevent unwanted triggers
        holder.checkBox.isChecked = grocery.isSelected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            grocery.isSelected = isChecked
        }

        // Set default quantity
        holder.quantityInput.setText(grocery.quantity.toString())

        // Update final price when quantity changes
        holder.quantityInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val quantity = holder.quantityInput.text.toString().toDoubleOrNull()
                if (quantity != null) {
                    grocery.quantity = quantity
                    holder.groceryFinalPrice.text = "Final Price: ₹${grocery.getFinalPrice()}"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return groceries.size
    }

    /**
     * ✅ Returns only the selected groceries.
     */
    fun getSelectedGroceries(): List<GroceryItem> {
        return groceries.filter { it.isSelected }
    }

    /**
     * ✅ Updates the list to show only **unselected groceries**.
     */
    fun updateUnselectedGroceries(newGroceries: List<GroceryItem>) {
        groceries.clear()
        groceries.addAll(newGroceries.filter { !it.isSelected })
        notifyDataSetChanged()
    }

    inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groceryName: TextView = itemView.findViewById(R.id.groceryName)
        val groceryPrice: TextView = itemView.findViewById(R.id.groceryPrice)
        val groceryDiscount: TextView = itemView.findViewById(R.id.groceryDiscount)
        val groceryFinalPrice: TextView = itemView.findViewById(R.id.groceryFinalPrice)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val quantityInput: EditText = itemView.findViewById(R.id.quantityInput)
    }
}
