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

package com.twoeightnine.root.xvii.chats.attachments.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.twoeightnine.root.xvii.base.BaseReachAdapter

abstract class BaseAttachmentsAdapter<T : Any, VB : ViewBinding, VH : BaseAttachmentsAdapter.BaseAttachmentViewHolder<T, VB>>(
        context: Context,
        loader: (Int) -> Unit
) : BaseReachAdapter<T, VH>(context, loader) {

    abstract fun getViewHolder(binding: VB): BaseAttachmentViewHolder<T, VB>

    abstract fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean): VB

    override fun createHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = inflateBinding(inflater, parent, false)
        return getViewHolder(binding) as VH
    }

    override fun bind(holder: VH, item: T) {
        (holder as? BaseAttachmentViewHolder<T, VB>)?.bind(item)
    }

    abstract class BaseAttachmentViewHolder<T, VB : ViewBinding>(private val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: T)
    }
}