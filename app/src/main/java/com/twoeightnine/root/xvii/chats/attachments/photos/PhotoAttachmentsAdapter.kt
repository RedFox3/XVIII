/*
 * xviii - messenger for vk
 * Copyright (C) 2021  TwoEightNine
 * Copyright (C) 2026  RedFox3
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

package com.twoeightnine.root.xvii.chats.attachments.photos

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.twoeightnine.root.xvii.chats.attachments.base.BaseAttachmentsAdapter
import com.twoeightnine.root.xvii.databinding.ItemPhotoAttachmentBinding
import com.twoeightnine.root.xvii.extensions.load
import com.twoeightnine.root.xvii.model.attachments.Photo

class PhotoAttachmentsAdapter(
        context: Context,
        loader: (Int) -> Unit,
        private val onClick: (Photo) -> Unit
) : BaseAttachmentsAdapter<Photo, ItemPhotoAttachmentBinding, PhotoAttachmentsAdapter.PhotoViewHolder>(context, loader) {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) =
        ItemPhotoAttachmentBinding.inflate(inflater, parent, attachToParent)

    override fun getViewHolder(binding: ItemPhotoAttachmentBinding) = PhotoViewHolder(binding)

    override fun createStubLoadItem() = Photo()

    companion object {
        const val ANIM_DURATION = 100L
        const val SCALE_THUMB_SELECTED = .95f
        const val SCALE_THUMB_DEFAULT = 1f
        const val SCALE_CHECK_SELECTED = 1f
        const val SCALE_CHECK_DEFAULT = 0f
    }

    inner class PhotoViewHolder(private val binding: ItemPhotoAttachmentBinding) : BaseAttachmentViewHolder<Photo, ItemPhotoAttachmentBinding>(binding) {

        override fun bind(item: Photo) {
            with(binding) {
                invalidateCheck(item, animate = false)
                ivPhoto.load(item.getMediumPhoto()?.url)
                root.setOnClickListener { // TODO: check does it work
                    if (multiSelectMode) {
                        val i = items[adapterPosition]
                        multiSelect(i)
                        invalidateCheck(i)
                    } else {
                        onClick(items[adapterPosition])
                    }
                }
            }
        }

        private fun invalidateCheck(item: Photo, animate: Boolean = true) {
            with(binding) {
                val selected = item in multiSelect
                val scaleThumb = if (selected) SCALE_THUMB_SELECTED else SCALE_THUMB_DEFAULT
                val scaleCheck = if (selected) SCALE_CHECK_SELECTED else SCALE_CHECK_DEFAULT
                val duration = if (animate) ANIM_DURATION else 0L
                ivPhoto.animate()
                        .scaleX(scaleThumb)
                        .scaleY(scaleThumb)
                        .setDuration(duration)
                        .start()
                ivCheck.animate()
                        .scaleX(scaleCheck)
                        .scaleY(scaleCheck)
                        .setDuration(duration)
                        .start()
            }
        }
    }
}