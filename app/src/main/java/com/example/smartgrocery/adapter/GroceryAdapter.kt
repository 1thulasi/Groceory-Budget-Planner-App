package com.example.smartgrocery.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.model.GroceryItem

class GroceryAdapter(
    private val groceryList: MutableList<GroceryItem>,
    private val onItemChecked: (GroceryItem, Boolean) -> Unit,
    private val onQuantityChanged: (GroceryItem) -> Unit
) : RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxSelect)
        private val itemName: TextView = itemView.findViewById(R.id.textItemName)
        private val itemPrice: TextView = itemView.findViewById(R.id.textItemPrice)
        private val itemDiscount: TextView = itemView.findViewById(R.id.textItemDiscount)
        private val itemFinalPrice: TextView = itemView.findViewById(R.id.textFinalPrice)
        private val itemQuantity: EditText = itemView.findViewById(R.id.editQuantity)

        fun bind(item: GroceryItem) {
            itemName.text = item.name
            itemPrice.text = "Price: ₹${item.price}"
            itemDiscount.text = "Discount: ${item.discount}%"
            updateFinalPrice(item)

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = item.isSelected
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isSelected = isChecked
                onItemChecked(item, isChecked)
            }

            itemQuantity.setText(item.quantity.toString())
            itemQuantity.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val quantity = s.toString().toDoubleOrNull() ?: 1.0
                    item.quantity = if (quantity > 0) quantity else 1.0
                    updateFinalPrice(item)
                    onQuantityChanged(item)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        private fun updateFinalPrice(item: GroceryItem) {
            val finalPrice = item.getFinalPrice()
            itemFinalPrice.text = "Final Price: ₹%.2f".format(finalPrice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false)
        return GroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(groceryList[position])
    }

    override fun getItemCount(): Int = groceryList.size
}
