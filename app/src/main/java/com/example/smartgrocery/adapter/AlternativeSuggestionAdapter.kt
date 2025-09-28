package com.example.smartgrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.model.GroceryItem

class AlternativeSuggestionAdapter(
    private val alternatives: List<GroceryItem>,
    private val onItemSelected: (GroceryItem) -> Unit
) : RecyclerView.Adapter<AlternativeSuggestionAdapter.AlternativeViewHolder>() {

    inner class AlternativeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvAltGroceryName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvAltPrice)
        val tvDiscount: TextView = itemView.findViewById(R.id.tvAltDiscount)
        val tvFinalPrice: TextView = itemView.findViewById(R.id.tvAltFinalPrice)
        val tvUnit: TextView = itemView.findViewById(R.id.tvAltUnit)
        val btnSelect: Button = itemView.findViewById(R.id.btnSelectAlternative)

        fun bind(item: GroceryItem) {
            tvName.text = item.name
            tvPrice.text = "Price: ₹%.2f".format(item.price)
            tvDiscount.text = "Discount: %.0f%%".format(item.discount)
            tvFinalPrice.text = "Final Price: ₹%.2f".format(item.getFinalPrice())
            tvUnit.text = "Unit: ${item.unit}"

            btnSelect.setOnClickListener {
                onItemSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlternativeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alternative, parent, false)
        return AlternativeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlternativeViewHolder, position: Int) {
        holder.bind(alternatives[position])
    }

    override fun getItemCount(): Int = alternatives.size
}
