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

package com.twoeightnine.root.xvii.chats.attachments.stickers.preview

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.databinding.DialogStickerPreviewBinding
import com.twoeightnine.root.xvii.extensions.load
import com.twoeightnine.root.xvii.model.attachments.Sticker
import com.twoeightnine.root.xvii.utils.stylize
import com.twoeightnine.root.xvii.views.TextInputAlertDialog

class StickerPreviewDialog(
        context: Context,
        private val sticker: Sticker,
        private val onKeywordsUpdated: (Int, List<String>) -> Unit
) : AlertDialog(context) {

    private val adapter by lazy {
        StickerKeywordsAdapter(context)
    }

    init {
        val binding = DialogStickerPreviewBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        with(binding) {
            ivSticker.load(sticker.photo512, placeholder = false)

            rvSuggestions.layoutManager = LinearLayoutManager(context)
            rvSuggestions.adapter = adapter
            adapter.addAll(sticker.keywords)

            rlAddKeyword.setOnClickListener {
                TextInputAlertDialog(context, "new keyword") { adapter.add(it) }.show()
            }
        }

        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok)) { _, _ ->
            saveChanges()
            dismiss()
        }
        setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel)) { _, _ ->
            dismiss()
        }
    }

    private fun saveChanges() {

    }

    override fun show() {
        super.show()
        stylize()
    }
}