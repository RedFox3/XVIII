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

package com.twoeightnine.root.xvii.chats.attachments.videos

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.twoeightnine.root.xvii.chats.attachments.base.BaseAttachmentsAdapter
import com.twoeightnine.root.xvii.databinding.ItemAttachmentsVideoBinding
import com.twoeightnine.root.xvii.extensions.load
import com.twoeightnine.root.xvii.model.attachments.Video
import com.twoeightnine.root.xvii.utils.secToTime
import global.msnthrp.xvii.uikit.extensions.setVisible

class VideoAttachmentsAdapter(
        context: Context,
        loader: (Int) -> Unit,
        private val onClick: (Video) -> Unit
) : BaseAttachmentsAdapter<Video, ItemAttachmentsVideoBinding, VideoAttachmentsAdapter.VideoViewHolder>(context, loader) {

    override fun getViewHolder(binding: ItemAttachmentsVideoBinding) = VideoViewHolder(binding)

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) =
        ItemAttachmentsVideoBinding.inflate(inflater, parent, attachToParent)

    override fun createStubLoadItem() = Video()

    inner class VideoViewHolder(private val binding: ItemAttachmentsVideoBinding)
        : BaseAttachmentViewHolder<Video, ItemAttachmentsVideoBinding>(binding) {

        override fun bind(item: Video) {
            with(binding) {
                tvDuration.setVisible(item.duration != 0)
                tvDuration.text = secToTime(item.duration)
                ivVideo.load(item.maxPhoto)
                tvTitle.text = item.title
                root.setOnClickListener { onClick(item) } // TODO: check does it work
            }
        }
    }
}