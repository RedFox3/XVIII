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

package com.twoeightnine.root.xvii.utils.contextpopup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.twoeightnine.root.xvii.databinding.ItemContextPopupBinding
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.paint
import global.msnthrp.xvii.uikit.base.adapters.BaseAdapter
import global.msnthrp.xvii.uikit.extensions.setVisible

class ContextPopupAdapter(
        context: Context,
        private val dialog: AlertDialog
) : BaseAdapter<ContextPopupItem, ContextPopupAdapter.ContextPopupItemHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ContextPopupItemHolder(ItemContextPopupBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ContextPopupItemHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ContextPopupItemHolder(private val binding: ItemContextPopupBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContextPopupItem) {
            with(binding) {
                tvTitle.text = context.getString(item.textRes)

                val hasIcon = item.iconRes != 0
                ivIcon.setVisible(hasIcon)
                if (hasIcon) {
                    ivIcon.setImageResource(item.iconRes)
                    ivIcon.paint(Munch.color.color)
                }
                rlBack.setOnClickListener {
                    dialog.dismiss()
                    item.onClick()
                }
            }
        }
    }
}