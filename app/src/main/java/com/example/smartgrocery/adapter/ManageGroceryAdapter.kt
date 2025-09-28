package com.example.smartgrocery.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.model.GroceryItem
import com.google.firebase.database.FirebaseDatabase

class ManageGroceryAdapter(
    private val context: Context,
    private val groceryList: MutableList<GroceryItem>
) : RecyclerView.Adapter<ManageGroceryAdapter.GroceryViewHolder>() {

    inner class GroceryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val price: TextView = view.findViewById(R.id.textPrice)
        val discount: TextView = view.findViewById(R.id.textDiscount)
        val finalPrice: TextView = view.findViewById(R.id.textFinalPrice)
        val btnEdit: Button = view.findViewById(R.id.buttonEdit)
        val btnDelete: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_grocery, parent, false)
        return GroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val item = groceryList[position]

        holder.name.text = item.name
        holder.price.text = "Price: ₹${item.price}"
        holder.discount.text = "Discount: ${item.discount}%"
        holder.finalPrice.text = "Final Price: ₹${item.getFinalPrice()}"

        Log.d("AdapterDebug", "Loaded item: ${item.name}, ₹${item.price}, ${item.discount}%")

        holder.btnEdit.setOnClickListener {
            showEditDialog(item, position)
        }

        holder.btnDelete.setOnClickListener {
            val itemId = item.id ?: return@setOnClickListener
            val dbRef = FirebaseDatabase.getInstance().getReference("groceries").child(itemId)
            dbRef.removeValue().addOnSuccessListener {
                groceryList.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = groceryList.size

    private fun showEditDialog(item: GroceryItem, position: Int) {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        val priceInput = EditText(context)
        priceInput.hint = "Enter new price"
        priceInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        priceInput.setText(item.price.toString())
        layout.addView(priceInput)

        val discountInput = EditText(context)
        discountInput.hint = "Enter new discount (%)"
        discountInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        discountInput.setText(item.discount.toString())
        layout.addView(discountInput)

        AlertDialog.Builder(context)
            .setTitle("Edit Grocery Item")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val newPrice = priceInput.text.toString().toDoubleOrNull() ?: item.price
                val newDiscount = discountInput.text.toString().toDoubleOrNull() ?: item.discount

                val itemId = item.id ?: return@setPositiveButton
                val dbRef = FirebaseDatabase.getInstance().getReference("groceries").child(itemId)
                val updates = mapOf("price" to newPrice, "discount" to newDiscount)

                dbRef.updateChildren(updates).addOnSuccessListener {
                    item.price = newPrice
                    item.discount = newDiscount
                    notifyItemChanged(position)
                    Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
