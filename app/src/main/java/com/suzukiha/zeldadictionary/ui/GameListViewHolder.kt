package com.suzukiha.zeldadictionary.ui

import android.graphics.Bitmap
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.model.Game
import com.suzukiha.zeldadictionary.util.isEnglish

class GameListViewHolder(
    itemView: View,
    onClickItem: (View, Long, String, String, String?) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var content: Game? = null

    private val parent: ConstraintLayout = itemView.findViewById(R.id.item_parent)
    private val name: AppCompatTextView = itemView.findViewById(R.id.name)
    private val thumbnail: AppCompatImageView = itemView.findViewById(R.id.thumbnail)
    private val description: AppCompatTextView = itemView.findViewById(R.id.description)
    private val releaseDate: AppCompatTextView = itemView.findViewById(R.id.releasedate)

    init {
        parent.setOnClickListener { view ->
            content?.let { game ->
                onClickItem.invoke(
                    view,
                    game.id.toLong(),
                    game.name!!,
                    game.description!!,
                    game.thumbnailUrl
                )
            }
        }
    }

    fun bind(game: Game) {
        content = game
        name.let {
            it.text = game.name
            it.contentDescription = game.name
            if (isEnglish()) {
                it.typeface = ResourcesCompat.getFont(it.context, R.font.hylia_serif_beta_regular)
            }
        }
        description.let {
            val description = if (game.description.isNullOrEmpty()) {
                description.context.getString(R.string.description_no_info)
            } else {
                game.description
            }
            it.text = description
            it.contentDescription = description
            if (isEnglish()) {
                it.typeface = ResourcesCompat.getFont(it.context, R.font.hylia_serif_beta_regular)
            }
        }
        releaseDate.let {
            it.text = game.releaseDate
            it.contentDescription = game.releaseDate
        }

        game.thumbnailUrl?.let {
            thumbnail.load(it) {
                crossfade(true)
                error(R.drawable.dictionary)
                bitmapConfig(Bitmap.Config.ARGB_8888)
                allowHardware(false)
            }
        }

        parent.transitionName = game.name
    }
}
