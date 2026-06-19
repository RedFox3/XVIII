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

package com.twoeightnine.root.xvii.utils

import global.msnthrp.xvii.core.accounts.model.Account
import global.msnthrp.xvii.data.dialogs.Dialog

object FakeData {

    const val ENABLED = false
    const val ENABLED_DIALOGS = false

    const val NAME_MINE = "mikhaïl bakounine"
    const val NAME_PERSON = "herzen"

    const val AVATAR_MINE = "https://i.pinimg.com/236x/14/ee/01/14ee015a8fde1a5d41f3f4536ff2ecd6.jpg"
    const val AVATAR_PERSON = "https://cdni.rbth.com/rbthmedia/images/web/en-rbth/images/2012-04/big/RIA-Novosti-gerzen-468.jpg"

    private const val TIME = 1619074800

    val dialogs = arrayListOf(Dialog(
        peerId = 0,
        messageId = 0,
        title = NAME_PERSON,
        photo = AVATAR_PERSON,
        text = "life has taught me to think, but thinking has not taught me to live",
        timeStamp = TIME - 863,
        isOut = true,
        isRead = true,
        unreadCount = 0,
        isOnline = true,
        isMute = false,
        isPinned = true,
        alias = null
    ), Dialog(
        peerId = 16,
        messageId = 0,
        title = "philipp \uD83C\uDDE9\uD83C\uDDEA",
        photo = "https://vk.com/images/camera_400.png",
        text = "sticker",
        timeStamp = TIME - 163,
        isOut = false,
        isRead = true,
        unreadCount = 0,
        isOnline = true,
        isMute = false,
        isPinned = false,
        alias = null
    ), Dialog(
        peerId = 18,
        messageId = 0,
        title = "kevin mittagessen",
        photo = "https://live.staticflickr.com/3080/3140160186_7fc2e77eb3_b.jpg",
        text = "nothing new btw",
        timeStamp = TIME - 666,
        isOut = true,
        isRead = true,
        unreadCount = 0,
        isOnline = true,
        isMute = false,
        isPinned = false,
        alias = null
    ), Dialog(
        peerId = 0,
        messageId = 0,
        title = NAME_MINE,
        photo = AVATAR_MINE,
        text = "3 photos",
        timeStamp = TIME - 1000,
        isOut = false,
        isRead = false,
        unreadCount = 0,
        isOnline = false,
        isMute = false,
        isPinned = false,
        alias = null
    ), Dialog(
        peerId = 777,
        messageId = 0,
        title = "IWA 289",
        photo = null,
        text = "bien sûr",
        timeStamp = TIME - 1112,
        isOut = false,
        isRead = false,
        unreadCount = 17,
        isOnline = false,
        isMute = true,
        isPinned = false,
        alias = null
    ), Dialog(
        peerId = 0,
        messageId = 0,
        title = "first international",
        photo = "https://i.redd.it/uuaz0o89pf541.png",
        text = "and the United States of America was represented by Cameron",
        timeStamp = TIME - 1313,
        isOut = true,
        isRead = false,
        unreadCount = 0,
        isOnline = false,
        isMute = false,
        isPinned = false,
        alias = null
    )
    )

    val accounts = arrayListOf(Account(
            userId = 1753175317,
            token = "stub",
            name = NAME_MINE,
            photo = AVATAR_MINE,
            isActive = true
    ), Account(
            userId = 1753171753,
            token = "stub",
            name = "kriepie fank",
            photo = "https://www.shared.com/content/images/2018/09/CCTV.jpg",
            isActive = false
    ), Account(
            userId = 1717531753,
            name = "pierre gardin",
            token = "stub",
            photo = "https://upload.wikimedia.org/wikipedia/commons/f/f6/Colour_grid_showing_256_color_image_composed_of_true_color_values.png",
            isActive = false
    ))
}