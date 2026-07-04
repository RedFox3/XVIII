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

package com.twoeightnine.root.xvii.chats.messages.chat

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twoeightnine.root.xvii.databinding.ItemUserMentionedBinding
import com.twoeightnine.root.xvii.extensions.getInitials
import com.twoeightnine.root.xvii.model.User
import global.msnthrp.xvii.uikit.base.adapters.BaseAdapter
import java.util.*

class MentionedMembersAdapter(
        context: Context,
        private val onClick: (User) -> Unit
) : BaseAdapter<User, MentionedMembersAdapter.MemberViewHolder>(context) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ) = MemberViewHolder(ItemUserMentionedBinding.inflate(inflater, parent, false))

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MemberViewHolder(private val binding: ItemUserMentionedBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: User) {
            with(binding) {
                tvName.text = member.fullName
                tvInfo.text = "@${member.getPageName()}"
                civPhoto.load(member.photo100, member.fullName.getInitials().toUpperCase(Locale.ROOT), id = member.id)

                root.setOnClickListener { onClick(items[adapterPosition]) }
            }
        }
    }
}
