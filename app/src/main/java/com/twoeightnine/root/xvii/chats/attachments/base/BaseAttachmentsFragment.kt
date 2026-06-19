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

package com.twoeightnine.root.xvii.chats.attachments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseFragment
import com.twoeightnine.root.xvii.databinding.FragmentAttachmentsBinding
import com.twoeightnine.root.xvii.model.Wrapper
import com.twoeightnine.root.xvii.utils.showError
import global.msnthrp.xvii.uikit.extensions.applyBottomInsetPadding
import global.msnthrp.xvii.uikit.extensions.hide
import global.msnthrp.xvii.uikit.extensions.show
import javax.inject.Inject

abstract class BaseAttachmentsFragment<T : Any, VB : ViewBinding> : BaseFragment<FragmentAttachmentsBinding>() {

    @Inject
    lateinit var viewModelFactory: BaseAttachmentsViewModel.Factory
    protected lateinit var viewModel: BaseAttachmentsViewModel<T>

    private val peerId by lazy { arguments?.getInt(ARG_PEER_ID) ?: 0 }

    abstract val adapter: BaseAttachmentsAdapter<T, VB, out BaseAttachmentsAdapter.BaseAttachmentViewHolder<T, VB>>

    abstract fun getLayoutManager(): RecyclerView.LayoutManager

    abstract fun getViewModelClass(): Class<out BaseAttachmentsViewModel<T>>

    abstract fun inject()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAttachmentsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inject()
        viewModel = ViewModelProviders.of(this, viewModelFactory)[getViewModelClass()]
        viewModel.peerId = peerId
        initRecycler()

        adapter.startLoading()

        binding.apply {
            progressBar.show()
            swipeRefresh.setOnRefreshListener {
                viewModel.reset()
                viewModel.loadAttachments()
                adapter.reset()
                adapter.startLoading()
            }

            rvAttachments.applyBottomInsetPadding()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getAttachments().observe(viewLifecycleOwner, ::updateList)
        viewModel.loadAttachments()
    }

    private fun updateList(data: Wrapper<ArrayList<T>>) {
        binding.apply {
            swipeRefresh.isRefreshing = false
            progressBar.hide()
        }
        if (data.data != null) {
            adapter.update(data.data)
        } else {
            showError(context, data.error ?: getString(R.string.error))
        }
    }

    fun loadMore(offset: Int) {
        viewModel.loadAttachments()
    }

    private fun initRecycler() {
        binding.apply {
            rvAttachments.layoutManager = getLayoutManager()
            rvAttachments.adapter = adapter
        }
    }

    companion object {
        const val ARG_PEER_ID = "peerId"
    }
}