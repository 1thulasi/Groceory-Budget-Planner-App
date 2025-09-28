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

class SavedGroceryAdapter(
    private val savedGroceryList: MutableList<GroceryItem>,
    private val onQuantityChanged: () -> Unit,
    private val onItemRemoved: () -> Unit
) : RecyclerView.Adapter<SavedGroceryAdapter.SavedGroceryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedGroceryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_saved_grocery, parent, false)
        return SavedGroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedGroceryViewHolder, position: Int) {
        val item = savedGroceryList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = savedGroceryList.size

    inner class SavedGroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groceryNameTextView: TextView = itemView.findViewById(R.id.groceryNameTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        private val discountTextView: TextView = itemView.findViewById(R.id.discountTextView)
        private val finalPriceTextView: TextView = itemView.findViewById(R.id.finalPriceTextView)
        private val quantityEditText: EditText = itemView.findViewById(R.id.quantityEditText)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(item: GroceryItem) {
            groceryNameTextView.text = item.name
            priceTextView.text = "Price: ₹${item.price}"
            discountTextView.text = "Discount: ${item.discount}%"
            finalPriceTextView.text = "Final Price: ₹${item.getFinalPrice()}"
            quantityEditText.setText(item.quantity.toString())

            quantityEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newQuantity = quantityEditText.text.toString().toDoubleOrNull() ?: item.quantity
                    if (newQuantity != item.quantity) {
                        item.quantity = newQuantity
                        finalPriceTextView.text = "Final Price: ₹${item.getFinalPrice()}"
                        onQuantityChanged()
                    }
                }
            }

            removeButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    savedGroceryList.removeAt(position)
                    notifyItemRemoved(position)
                    onItemRemoved()
                }
            }
        }
    }
}
