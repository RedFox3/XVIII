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

package com.twoeightnine.root.xvii.friends.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.twoeightnine.root.xvii.App
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseFragment
import com.twoeightnine.root.xvii.chatowner.ChatOwnerFactory
import com.twoeightnine.root.xvii.databinding.FragmentFriendsBinding
import com.twoeightnine.root.xvii.friends.adapters.FriendsAdapter
import com.twoeightnine.root.xvii.friends.viewmodel.FriendsViewModel
import com.twoeightnine.root.xvii.model.User
import com.twoeightnine.root.xvii.model.Wrapper
import com.twoeightnine.root.xvii.utils.AppBarLifter
import com.twoeightnine.root.xvii.utils.showError
import global.msnthrp.xvii.uikit.extensions.applyBottomInsetPadding
import global.msnthrp.xvii.uikit.extensions.hide
import global.msnthrp.xvii.uikit.extensions.show
import javax.inject.Inject

class FriendsFragment : BaseFragment<FragmentFriendsBinding>() {

    @Inject
    lateinit var viewModelFactory: FriendsViewModel.Factory
    private lateinit var viewModel: FriendsViewModel

    private val adapter by lazy {
        FriendsAdapter(requireContext(), ::onClick, ::loadMore)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFriendsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent?.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[FriendsViewModel::class.java]

        adapter.startLoading()

        binding.apply {
            progressBar.show()
            rvFriends.layoutManager = LinearLayoutManager(context)
            rvFriends.adapter = adapter
            rvFriends.addOnScrollListener(AppBarLifter(xviiToolbar))

            swipeRefresh.setOnRefreshListener {
                viewModel.loadFriends()
                adapter.reset()
                adapter.startLoading()
            }
            rvFriends.applyBottomInsetPadding()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getFriends().observe(viewLifecycleOwner, ::updateFriends)
        viewModel.loadFriends()
    }

    private fun updateFriends(data: Wrapper<ArrayList<User>>) {
        binding.swipeRefresh.isRefreshing = false
        binding.progressBar.hide()
        if (data.data != null) {
            adapter.update(data.data)
        } else {
            showError(context, data.error ?: getString(R.string.error))
        }
    }

    private fun loadMore(offset: Int) {
        viewModel.loadFriends(offset)
    }

    private fun onClick(user: User) {
        ChatOwnerFactory.launch(context, user.id)
    }

    companion object {
        fun newInstance() = FriendsFragment()
    }
}