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

package com.twoeightnine.root.xvii.chats.attachments.links

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.twoeightnine.root.xvii.chats.attachments.base.BaseAttachmentsAdapter
import com.twoeightnine.root.xvii.databinding.ItemAttachmentsLinkBinding
import com.twoeightnine.root.xvii.extensions.load
import com.twoeightnine.root.xvii.model.attachments.Link

class LinkAttachmentsAdapter(
        context: Context,
        loader: (Int) -> Unit,
        private val onClick: (Link) -> Unit
) : BaseAttachmentsAdapter<Link, ItemAttachmentsLinkBinding, LinkAttachmentsAdapter.LinkAttachmentsViewHolder>(context, loader) {

    override fun getViewHolder(binding: ItemAttachmentsLinkBinding) = LinkAttachmentsViewHolder(binding)

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) =
        ItemAttachmentsLinkBinding.inflate(inflater, parent, attachToParent)

    override fun createStubLoadItem() = Link()

    inner class LinkAttachmentsViewHolder(private val binding: ItemAttachmentsLinkBinding)
        : BaseAttachmentViewHolder<Link, ItemAttachmentsLinkBinding>(binding) {

        override fun bind(item: Link) {
            with(binding) {
                tvTitle.text = item.title
                tvCaption.text = item.caption
                ivPhoto.load(item.photo?.getSmallPhoto()?.url)
                root.setOnClickListener { onClick(items[adapterPosition]) } // TODO: check does it work
            }
        }
    }
}