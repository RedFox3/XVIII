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

package com.twoeightnine.root.xvii.views

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.twoeightnine.root.xvii.databinding.DialogLoadingBinding
import com.twoeightnine.root.xvii.utils.stylize
import global.msnthrp.xvii.uikit.extensions.hide

class LoadingDialog(context: Context,
                    text: String = "",
                    cancelable: Boolean = false) : AlertDialog(context) {

    init {
        DialogLoadingBinding.inflate(LayoutInflater.from(context)).apply {
            if (text.isNotEmpty()) {
                tvTitle.text = text
            } else {
                tvTitle.hide()
            }
            setCancelable(cancelable)
            setView(root)
        }
    }

    override fun show() {
        super.show()
        stylize()
    }
}