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

package com.twoeightnine.root.xvii.chats.attachments.docs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.twoeightnine.root.xvii.chats.attachments.base.BaseAttachmentsAdapter
import com.twoeightnine.root.xvii.databinding.ItemAttachmentsDocBinding
import com.twoeightnine.root.xvii.extensions.load
import com.twoeightnine.root.xvii.model.attachments.Doc
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.utils.getSize
import global.msnthrp.xvii.uikit.extensions.setVisible
import global.msnthrp.xvii.uikit.utils.color.DocColors

class DocAttachmentsAdapter(
        context: Context,
        loader: (Int) -> Unit,
        private val onClick: (Doc) -> Unit
) : BaseAttachmentsAdapter<Doc, ItemAttachmentsDocBinding, DocAttachmentsAdapter.DocViewHolder>(context, loader) {

    override fun getViewHolder(binding: ItemAttachmentsDocBinding) = DocViewHolder(binding)

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) =
        ItemAttachmentsDocBinding.inflate(inflater, parent, attachToParent)

    override fun createStubLoadItem() = Doc()

    inner class DocViewHolder(private val binding: ItemAttachmentsDocBinding) : BaseAttachmentViewHolder<Doc, ItemAttachmentsDocBinding>(binding) {

        override fun bind(item: Doc) {
            with(binding) {
                val extSafe = item.ext ?: ""
                tvExt.text = prettifyExt(extSafe)
                tvTitle.text = item.title
                tvSize.text = getSize(root.context.resources, item.size) // TODO: check does it work

                val preview = item.preview?.photo?.getSmallPreview()?.src
                val hasPreview = preview != null

                ivDocPreview.setVisible(hasPreview)
                tvExt.setVisible(!hasPreview)

                if (preview != null) {
                    ivDocPreview.load(preview)
                } else {
                    ivDocPreview.setImageDrawable(null)
                }
                cvDocPreview.setCardBackgroundColor(
                        DocColors.getColorByExtension(extSafe) ?: Munch.color.color
                )
                root.setOnClickListener { onClick(items[adapterPosition]) } // TODO: check does it work
            }
        }

        private fun prettifyExt(ext: String): String = when {
            ext.length <= 4 -> ext
            else -> {
                val cropped = ext.take(8)
                val center = cropped.length / 2
                "${cropped.substring(0, center)}\n${cropped.substring(center)}"
            }
        }
    }
}