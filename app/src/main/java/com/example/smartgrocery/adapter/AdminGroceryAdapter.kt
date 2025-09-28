package com.example.smartgrocery.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.model.GroceryItem
import com.google.firebase.database.FirebaseDatabase

class AdminGroceryAdapter(
    private val groceryList: MutableList<GroceryItem>
) : RecyclerView.Adapter<AdminGroceryAdapter.AdminGroceryViewHolder>() {

    inner class AdminGroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textItemName)
        val price: TextView = itemView.findViewById(R.id.textItemPrice)
        val discount: TextView = itemView.findViewById(R.id.textItemDiscount)
        val finalPrice: TextView = itemView.findViewById(R.id.textFinalPrice)
        val editBtn: ImageButton = itemView.findViewById(R.id.btnEdit)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminGroceryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_grocery, parent, false)
        return AdminGroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminGroceryViewHolder, position: Int) {
        val item = groceryList[position]
        holder.name.text = item.name
        holder.price.text = "Price: ₹${item.price}"
        holder.discount.text = "Discount: ₹${item.discount}"
        holder.finalPrice.text = "Final Price: ₹${item.getFinalPrice()}"

        holder.editBtn.setOnClickListener {
            showEditDialog(holder.itemView, item)
        }

        holder.deleteBtn.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("groceries").child(item.id)
            dbRef.removeValue()
            groceryList.removeAt(position)
            notifyItemRemoved(position)
            Toast.makeText(holder.itemView.context, "Item deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = groceryList.size

    private fun showEditDialog(view: View, item: GroceryItem) {
        val context = view.context
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_grocery, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
        val priceEdit = dialogView.findViewById<EditText>(R.id.editPrice)
        val discountEdit = dialogView.findViewById<EditText>(R.id.editDiscount)

        nameEdit.setText(item.name)
        priceEdit.setText(item.price.toString())
        discountEdit.setText(item.discount.toString())

        AlertDialog.Builder(context)
            .setTitle("Edit Grocery")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updatedName = nameEdit.text.toString()
                val updatedPrice = priceEdit.text.toString().toDoubleOrNull() ?: 0.0
                val updatedDiscount = discountEdit.text.toString().toDoubleOrNull() ?: 0.0

                val updatedItem = GroceryItem(
                    id = item.id,
                    name = updatedName,
                    price = updatedPrice,
                    discount = updatedDiscount
                )

                val dbRef = FirebaseDatabase.getInstance().getReference("groceries").child(item.id)
                dbRef.setValue(updatedItem)
                val index = groceryList.indexOfFirst { it.id == item.id }
                if (index != -1) {
                    groceryList[index] = updatedItem
                    notifyItemChanged(index)
                }
                Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
