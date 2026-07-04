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

package com.twoeightnine.root.xvii.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twoeightnine.root.xvii.databinding.ItemDialogSearchBinding
import com.twoeightnine.root.xvii.extensions.load
import com.twoeightnine.root.xvii.managers.Prefs
import global.msnthrp.xvii.data.dialogs.Dialog
import global.msnthrp.xvii.uikit.base.adapters.BaseAdapter
import global.msnthrp.xvii.uikit.extensions.hide
import global.msnthrp.xvii.uikit.extensions.lowerIf

class SearchAdapter(
        context: Context,
        private val onClick: (Dialog) -> Unit,
        private val onLongClick: (Dialog) -> Unit
) : BaseAdapter<Dialog, SearchAdapter.SearchViewHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SearchViewHolder(ItemDialogSearchBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class SearchViewHolder(private val binding: ItemDialogSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dialog: Dialog) {
            with(binding) {
                civPhoto.load(dialog.photo)
                tvTitle.text = dialog.title
                tvTitle.lowerIf(Prefs.lowerTexts)
                ivOnlineDot.hide() // due to this list is not autorefreshable

                rlItemContainer.setOnClickListener {
                    items.getOrNull(adapterPosition)
                            ?.also(onClick)
                }
                rlItemContainer.setOnLongClickListener {
                    items.getOrNull(adapterPosition)
                            ?.also(onLongClick)
                    true
                }
            }
        }
    }
}