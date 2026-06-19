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
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.databinding.DialogCommentBinding
import com.twoeightnine.root.xvii.utils.stylize
import global.msnthrp.xvii.uikit.extensions.asText

class TextInputAlertDialog(
    context: Context,
    hint: String,
    presetText: String = "",
    private val onCommentAdded: (String) -> Unit
) : AlertDialog(context) {

    private val binding = DialogCommentBinding.inflate(LayoutInflater.from(context))

    init {
        with(binding) {
            etComment.hint = hint
            etComment.setText(presetText)
        }
        setView(binding.root)
        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok)) { _, _ ->
            onCommentAdded(binding.etComment.asText())
            dismiss()
        }
    }

    override fun show() {
        super.show()
        stylize()
    }
}