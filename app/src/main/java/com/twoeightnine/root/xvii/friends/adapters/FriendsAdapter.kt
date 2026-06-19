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

package com.twoeightnine.root.xvii.friends.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twoeightnine.root.xvii.base.BaseReachAdapter
import com.twoeightnine.root.xvii.databinding.ItemUserBinding
import com.twoeightnine.root.xvii.extensions.getInitials
import com.twoeightnine.root.xvii.managers.Prefs
import com.twoeightnine.root.xvii.model.User
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.paint
import com.twoeightnine.root.xvii.utils.LastSeenUtils
import global.msnthrp.xvii.uikit.extensions.lowerIf
import global.msnthrp.xvii.uikit.extensions.setVisible

class FriendsAdapter(context: Context,
                     private val onClick: (User) -> Unit,
                     onLoaded: (Int) -> Unit
) : BaseReachAdapter<User, FriendsAdapter.FriendViewHolder>(context, onLoaded) {

    var firstItemPadding = 0

    override fun createHolder(parent: ViewGroup, viewType: Int) =
            FriendViewHolder(ItemUserBinding.inflate(inflater, parent, false))


    override fun bind(holder: FriendViewHolder, item: User) {
        holder.bind(item, items[0] == item)
    }

    override fun createStubLoadItem() = User()

    inner class FriendViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User, isFirst: Boolean) {
            with(binding) {
                val topPadding = if (isFirst) firstItemPadding else 0
                root.setPadding(0, topPadding, 0, 0)

                civPhoto.load(user.photo100, user.fullName.getInitials(), id = user.id)
                ivOnlineDot.setVisible(user.isOnline)
                ivOnlineDot.paint(Munch.color.color)
                tvName.text = user.fullName
                tvName.lowerIf(Prefs.lowerTexts)

                user.lastSeen?.also {
                    tvInfo.text = LastSeenUtils.getFull(context, user.isOnline, it.time, it.platform)
                }
                root.setOnClickListener {
                    items.getOrNull(adapterPosition)?.also(onClick)
                }
            }
        }
    }

}
