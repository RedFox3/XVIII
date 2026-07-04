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

package com.twoeightnine.root.xvii.features.datausage

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twoeightnine.root.xvii.databinding.ItemDataUsageEventBinding
import com.twoeightnine.root.xvii.network.datausage.DataUsageEvent
import com.twoeightnine.root.xvii.utils.getSize
import com.twoeightnine.root.xvii.utils.getTime
import global.msnthrp.xvii.uikit.base.adapters.BaseAdapter

class DataUsageAdapter(context: Context) : BaseAdapter<DataUsageEvent, DataUsageAdapter.DataUsageViewHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = DataUsageViewHolder(ItemDataUsageEventBinding.inflate(inflater, parent, false))

    override fun onBindViewHolder(holder: DataUsageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class DataUsageViewHolder(private val binding: ItemDataUsageEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dataUsageEvent: DataUsageEvent) {
            with(binding) {
                var name = dataUsageEvent.name
                name = name.replace("api.vk.com/method/", "")
                tvName.text = name
                tvTime.text = getTime(dataUsageEvent.timeStamp, withSeconds = true)
                tvOutgoing.text = getSize(root.resources, dataUsageEvent.requestSize.toInt())
                tvIncoming.text = getSize(root.resources, dataUsageEvent.responseSize.toInt())
            }
        }
    }
}
