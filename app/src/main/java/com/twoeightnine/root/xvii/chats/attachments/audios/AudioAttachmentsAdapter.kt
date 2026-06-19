/*
 * xvii - messenger for vk
 * Copyright (C) 2021  TwoEightNine
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.twoeightnine.root.xvii.chats.attachments.audios

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.background.music.models.Track
import com.twoeightnine.root.xvii.chats.attachments.base.BaseAttachmentsAdapter
import com.twoeightnine.root.xvii.databinding.ItemAttachmentsTrackBinding
import com.twoeightnine.root.xvii.model.attachments.Audio
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.paint
import com.twoeightnine.root.xvii.utils.secToTime
import global.msnthrp.xvii.uikit.extensions.hide
import global.msnthrp.xvii.uikit.extensions.setVisible
import global.msnthrp.xvii.uikit.extensions.show

class AudioAttachmentsAdapter(
        context: Context,
        loader: (Int) -> Unit,
        private val onClick: (Track) -> Unit,
        private val onLongClick: (Track) -> Unit,
        private val onDownload: (Track) -> Unit,
        private val cacheMode: Boolean = false

) : BaseAttachmentsAdapter<Track, ItemAttachmentsTrackBinding, AudioAttachmentsAdapter.AudioViewHolder>(context, loader) {

    var played: Track? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getViewHolder(binding: ItemAttachmentsTrackBinding) = AudioViewHolder(binding)

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) =
        ItemAttachmentsTrackBinding.inflate(inflater, parent, attachToParent)

    override fun createStubLoadItem() = Track(Audio())

    inner class AudioViewHolder(private val binding: ItemAttachmentsTrackBinding)
        : BaseAttachmentViewHolder<Track, ItemAttachmentsTrackBinding>(binding) {

        override fun bind(item: Track) {
            with(binding) {
                val icon = if (item == played) {
                    val dPause = ContextCompat.getDrawable(context, R.drawable.ic_pause)
                    dPause?.paint(Munch.color.color)
                    dPause
                } else {
                    val dPlay = ContextCompat.getDrawable(context, R.drawable.ic_play)
                    dPlay?.paint(Munch.color.color)
                    dPlay
                }
                ivDownload.paint(Munch.color.color)
                ivCached.paint(Munch.color.color)

                val cached = item.isCached()
                ivDownload.setVisible(!cached && cacheMode)
                ivCached.setVisible(cached && cacheMode)
                progressBar.hide()
                ivButton.setImageDrawable(icon)
                tvTitle.text = item.audio.title
                tvArtist.text = item.audio.artist
                tvDuration.text = secToTime(item.audio.duration)
                root.setOnClickListener { onClick(items[adapterPosition]) } // TODO: test this code
                root.setOnLongClickListener { // TODO: test this code
                    onLongClick(items[adapterPosition])
                    true
                }
                ivDownload.setOnClickListener {
                    progressBar.show()
                    ivDownload.hide()
                    onDownload(items[adapterPosition])
                }
            }
        }
    }
}