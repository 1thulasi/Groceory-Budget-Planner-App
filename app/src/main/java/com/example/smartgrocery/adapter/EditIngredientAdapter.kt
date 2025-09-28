package com.example.smartgrocery.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R

class EditIngredientAdapter(private val ingredients: MutableList<Ingredient>) :
    RecyclerView.Adapter<EditIngredientAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editName: EditText = view.findViewById(R.id.editIngredientName)
        val editQuantity: EditText = view.findViewById(R.id.editIngredientQuantity)
        val editUnit: EditText = view.findViewById(R.id.editIngredientUnit)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemoveIngredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_edit_ingredient, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = ingredients.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]

        holder.editName.setText(ingredient.name)
        holder.editQuantity.setText(ingredient.quantity)
        holder.editUnit.setText(ingredient.unit)

        holder.editName.addTextChangedListener(SimpleTextWatcher { text -> ingredient.name = text })
        holder.editQuantity.addTextChangedListener(SimpleTextWatcher { text -> ingredient.quantity = text })
        holder.editUnit.addTextChangedListener(SimpleTextWatcher { text -> ingredient.unit = text })

        holder.btnRemove.setOnClickListener {
            ingredients.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, ingredients.size)
        }
    }
}
