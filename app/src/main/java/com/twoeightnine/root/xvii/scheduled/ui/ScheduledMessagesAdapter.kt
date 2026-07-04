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

package com.twoeightnine.root.xvii.scheduled.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.databinding.ItemScheduledMessageBinding
import com.twoeightnine.root.xvii.managers.Prefs
import com.twoeightnine.root.xvii.utils.getTime
import global.msnthrp.xvii.data.scheduled.ScheduledMessage
import global.msnthrp.xvii.uikit.base.adapters.BaseAdapter
import global.msnthrp.xvii.uikit.extensions.lowerIf
import global.msnthrp.xvii.uikit.extensions.setVisible

class ScheduledMessagesAdapter(
        context: Context,
        private val onClick: (ScheduledMessage) -> Unit
) : BaseAdapter<ScheduledMessage, ScheduledMessagesAdapter.ScheduledMessageViewHolder>(context) {

    private var peersMap: Map<Int, String> = hashMapOf()

    fun updatePeersMap(peersMap: Map<Int, String>) {
        this.peersMap = peersMap
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ) = ScheduledMessageViewHolder(ItemScheduledMessageBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ScheduledMessageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ScheduledMessageViewHolder(private val binding: ItemScheduledMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(scheduledMessage: ScheduledMessage) {
            with(binding) {
                tvPeer.text = peersMap[scheduledMessage.peerId] ?: "id${scheduledMessage.peerId}"
                tvPeer.lowerIf(Prefs.lowerTexts)

                tvText.setVisible(scheduledMessage.text.isNotBlank())
                tvText.text = scheduledMessage.text

                tvWhen.text = getTime((scheduledMessage.whenMs / 1000).toInt(), withSeconds = Prefs.showSeconds)

                val info = getInfo(scheduledMessage.attachments, scheduledMessage.forwardedMessages)
                tvInfo.setVisible(info != null)
                tvInfo.text = info

                root.setOnClickListener { onClick(items[adapterPosition]) }
            }
        }

        private fun getInfo(attachments: String?, forwardedMessages: String?): String? {
            if (attachments.isNullOrBlank() && forwardedMessages.isNullOrBlank()) {
                return null
            }
            val attachmentsCount = attachments?.split(",")?.size ?: 0
            val hasForwardedMessages = forwardedMessages?.isNotBlank() == true
            val info = StringBuilder()
            if (attachmentsCount != 0) {
                info.append(context.resources.getQuantityString(R.plurals.attachments, attachmentsCount, attachmentsCount))
                if (hasForwardedMessages) {
                    info.append(", ")
                }
            }
            if (hasForwardedMessages) {
                info.append(context.getString(R.string.scheduled_messages_has_fwd_messages))
            }
            return info.toString()
        }
    }
}