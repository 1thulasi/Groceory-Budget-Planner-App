package com.example.smartgrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.model.GroceryItem

class SelectedGroceriesAdapter(
    private val selectedGroceries: MutableList<GroceryItem>,
    private val onQuantityChanged: (GroceryItem) -> Unit,
    private val onRemoveItem: (GroceryItem) -> Unit
) : RecyclerView.Adapter<SelectedGroceriesAdapter.ViewHolder>() {

    private var isEditing = false
    private var isRemoving = false

    fun enableEditing(enable: Boolean) {
        isEditing = enable
        isRemoving = false
        notifyDataSetChanged()
    }

    fun enableRemoving(enable: Boolean) {
        isRemoving = enable
        isEditing = false
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_grocery, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groceryItem = selectedGroceries[position]
        holder.bind(groceryItem)
    }

    override fun getItemCount(): Int = selectedGroceries.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.groceryNameTextView)
        private val quantityEditText: EditText = itemView.findViewById(R.id.quantityEditText)
        private val priceTextView: TextView = itemView.findViewById(R.id.finalPriceTextView)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(groceryItem: GroceryItem) {
            nameTextView.text = groceryItem.name
            quantityEditText.setText(groceryItem.quantity.toString())
            priceTextView.text = "₹%.2f".format(groceryItem.getFinalPrice())

            quantityEditText.isEnabled = isEditing
            removeButton.visibility = if (isRemoving) View.VISIBLE else View.GONE

            quantityEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newQuantity = quantityEditText.text.toString().toDoubleOrNull()
                    if (newQuantity != null && newQuantity != groceryItem.quantity) {
                        groceryItem.quantity = newQuantity
                        priceTextView.text = "₹%.2f".format(groceryItem.getFinalPrice()) // Corrected reference
                        onQuantityChanged(groceryItem)
                        notifyDataSetChanged() // Refresh UI
                    }
                }
            }

            removeButton.setOnClickListener {
                onRemoveItem(groceryItem)
                notifyDataSetChanged() // Refresh UI after removal
            }
        }
    }
}
