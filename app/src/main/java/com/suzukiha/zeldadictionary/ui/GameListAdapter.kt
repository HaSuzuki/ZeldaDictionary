package com.suzukiha.zeldadictionary.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.model.Game

class GameListAdapter(
    private val onClickItem: (
        View, Long, String, String, String?
    ) -> Unit
) : ListAdapter<Game, GameListViewHolder>(GameDiff) {

    private lateinit var inflater: LayoutInflater
    private var isInflated: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        if (!isInflated) {
            inflater = LayoutInflater.from(parent.context)
            isInflated = true
        }
        return GameListViewHolder(
            itemView = inflater.inflate(
                R.layout.game_item,
                parent,
                false
            ),
            onClickItem = onClickItem
        )
    }

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        holder.bind(game = getItem(position))
    }
}

object GameDiff : DiffUtil.ItemCallback<Game>() {
    override fun areItemsTheSame(oldItem: Game, newItem: Game) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem
}