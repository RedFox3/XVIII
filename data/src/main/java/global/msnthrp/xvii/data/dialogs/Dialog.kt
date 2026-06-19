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

package global.msnthrp.xvii.data.dialogs

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "dialogs")
data class Dialog(
        @PrimaryKey
        val peerId: Int,
        var messageId: Int,
        val title: String,
        val photo: String?,
        var text: String,
        var timeStamp: Int,
        var isOut: Boolean,
        var isRead: Boolean,
        var unreadCount: Int,
        var isOnline: Boolean,
        var isMute: Boolean,
        var isPinned: Boolean,
        var alias: String?
) : Parcelable {

    val aliasOrTitle: String
        get() = alias ?: title

}