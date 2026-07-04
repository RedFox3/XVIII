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

package com.twoeightnine.root.xvii.journal.online

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.twoeightnine.root.xvii.base.BaseBottomSheet
import com.twoeightnine.root.xvii.databinding.FragmentJournalOnlineBinding
import com.twoeightnine.root.xvii.journal.online.model.OnlineInfo
import com.twoeightnine.root.xvii.managers.Prefs
import global.msnthrp.xvii.uikit.extensions.lowerIf

class JournalOnlineBottomSheet private constructor(): BaseBottomSheet<FragmentJournalOnlineBinding>() {

    private val onlineInfo by lazy {
        arguments?.getParcelable<OnlineInfo>(ARG_DATA)
    }
    private val adapter by lazy {
        OnlineEventAdapter(requireContext())
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentJournalOnlineBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvName.text = onlineInfo?.userName
            tvName.lowerIf(Prefs.lowerTexts)

            rvEvents.layoutManager = LinearLayoutManager(requireContext())
                .apply { stackFromEnd = true }
            rvEvents.adapter = adapter
        }
        onlineInfo?.events?.also(adapter::update)
    }

    companion object {

        private const val ARG_DATA = "data"

        fun newInstance(onlineInfo: OnlineInfo) = JournalOnlineBottomSheet().apply {
            arguments = bundleOf(
                    ARG_DATA to onlineInfo
            )
        }
    }
}