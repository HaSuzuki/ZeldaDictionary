package com.suzukiha.zeldadictionary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.model.Game

class WorkedOnGameListAdapter : ListAdapter<Game, WorkedOnGameListViewHolder>(GameDiff) {

    private lateinit var inflater: LayoutInflater
    private var isInflated: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkedOnGameListViewHolder {
        if (!isInflated) {
            inflater = LayoutInflater.from(parent.context)
            isInflated = true
        }
        return WorkedOnGameListViewHolder(
            itemView = inflater.inflate(
                R.layout.worked_on_game_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WorkedOnGameListViewHolder, position: Int) {
        holder.bind(game = getItem(position))
    }
}
