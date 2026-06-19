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

package com.twoeightnine.root.xvii.pin.fake.alarm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twoeightnine.root.xvii.databinding.ItemAlarmBinding
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.paint
import com.twoeightnine.root.xvii.utils.secToTime
import global.msnthrp.xvii.uikit.base.adapters.BaseAdapter

class AlarmAdapter(
        context: Context,
        private val onAllEnabled: () -> Unit
) : BaseAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(context) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ) = AlarmViewHolder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(items[position])
    }

    private fun notifyEnabledChanged() {
        items.forEach { alarm ->
            if (!alarm.enabled) return
        }
        // every alarm is enabled here
        onAllEnabled()
    }

    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alarm: Alarm) {
            with(binding) {
                tvTime.text = secToTime(alarm.time)
                swEnabled.isChecked = alarm.enabled
                cbEveryDay.isChecked = !alarm.onlyOnce

                swEnabled.setOnCheckedChangeListener { _, isChecked ->
                    alarm.enabled = isChecked
                    notifyEnabledChanged()
                }
                swEnabled.paint(Munch.color.color)
                cbEveryDay.paint(Munch.color.color)
            }
        }
    }
}