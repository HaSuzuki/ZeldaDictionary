package com.suzukiha.zeldadictionary.ui

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.model.Staff

class GameDetailViewHolder(
    itemView: View,
    onClickItem: (View, Staff) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var content: Staff? = null

    private val parent: ConstraintLayout = itemView.findViewById(R.id.parent)
    private val icon: AppCompatImageView = itemView.findViewById(R.id.icon)
    private val name: AppCompatTextView = itemView.findViewById(R.id.name)

    init {
        parent.setOnClickListener { view ->
            content?.let { staff ->
                onClickItem.invoke(
                    view,
                    staff
                )
            }
        }
    }

    fun bind(staff: Staff) {
        content = staff
        name.let {
            it.text = staff.name
            it.contentDescription = staff.name
        }
    }
}
