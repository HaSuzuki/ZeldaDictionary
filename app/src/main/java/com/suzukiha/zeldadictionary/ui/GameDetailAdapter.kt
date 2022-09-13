package com.suzukiha.zeldadictionary.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.model.Staff

class GameDetailAdapter(
    private val onClickItem: (View, Staff) -> Unit
) : ListAdapter<Staff, GameDetailViewHolder>(StaffDiff) {

    private lateinit var inflater: LayoutInflater
    private var isInflated: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameDetailViewHolder {
        if (!isInflated) {
            inflater = LayoutInflater.from(parent.context)
            isInflated = true
        }
        return GameDetailViewHolder(
            itemView = inflater.inflate(
                R.layout.staff_item,
                parent,
                false
            ),
            onClickItem = onClickItem
        )
    }

    override fun onBindViewHolder(holder: GameDetailViewHolder, position: Int) {
        holder.bind(staff = getItem(position))
    }
}

object StaffDiff : DiffUtil.ItemCallback<Staff>() {
    override fun areItemsTheSame(oldItem: Staff, newItem: Staff) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Staff, newItem: Staff) = oldItem == newItem
}
