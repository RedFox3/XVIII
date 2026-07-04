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

package com.twoeightnine.root.xvii.chats.messages.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseFragment
import com.twoeightnine.root.xvii.base.FragmentPlacementActivity.Companion.startFragment
import com.twoeightnine.root.xvii.chats.attachments.AttachmentsInflater
import com.twoeightnine.root.xvii.chats.messages.Interaction
import com.twoeightnine.root.xvii.databinding.FragmentChatBinding
import com.twoeightnine.root.xvii.dialogs.fragments.DialogsForwardFragment
import com.twoeightnine.root.xvii.model.Wrapper
import com.twoeightnine.root.xvii.utils.applyCompletableSchedulers
import com.twoeightnine.root.xvii.utils.getDate
import com.twoeightnine.root.xvii.utils.showError
import global.msnthrp.xvii.uikit.extensions.*
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class BaseMessagesFragment<VM : BaseMessagesViewModel> : BaseFragment<FragmentChatBinding>() {

    @Inject
    lateinit var viewModelFactory: BaseMessagesViewModel.Factory
    protected lateinit var viewModel: VM

    private val dateScroller = RecyclerDateScroller()
    private val listScrollListener = ListScrollListener()

    protected val adapter by lazy {
        MessagesAdapter(
                requireContext(),
                ::loadMore,
                getAdapterCallback(),
                getAttachmentsCallback(),
                getAdapterSettings()
        )
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentChatBinding {
        return FragmentChatBinding.inflate(inflater, container, false)
    }

    abstract fun getViewModelClass(): Class<VM>

    abstract fun inject()

    abstract fun getAdapterCallback(): MessagesAdapter.Callback

    abstract fun getAttachmentsCallback(): AttachmentsInflater.Callback

    abstract fun getAdapterSettings(): MessagesAdapter.Settings

    protected open fun prepareViewModel() {}

    /**
     * handle 'scrolled to bottom' events
     */
    protected open fun onScrolled(isAtBottom: Boolean) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        inject()
        initRecyclerView()
        initMultiAction()
        viewModel = ViewModelProviders.of(this, viewModelFactory)[getViewModelClass()]
        prepareViewModel()
        adapter.startLoading()

        binding.progressBar.show()
        binding.xviiToolbar.isLifted = true
        binding.swipeContainer.setOnRefreshListener {
            loadMore(0)
            adapter.reset()
            adapter.startLoading()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getInteraction().observe(viewLifecycleOwner, ::updateMessages2)
        viewModel.loadMessages()
    }

    protected fun getSelectedMessageIds() = adapter.multiSelect
            .joinToString(separator = ",", transform = { it.message.id.toString() })

    private fun updateMessages2(data: Wrapper<Interaction>) {
        binding.swipeContainer.isRefreshing = false
        binding.progressBar.hide()
        if (data.data == null) {
            showError(context, data.error)
        }
        val interaction = data.data ?: return

        try {
            when (interaction.type) {
                Interaction.Type.CLEAR -> {
                    adapter.clear()
                }
                Interaction.Type.ADD -> {
                    val firstLoad = adapter.isEmpty
                    val isAtEnd = adapter.isAtBottom(binding.rvChatList.layoutManager as? LinearLayoutManager)
                    adapter.addAll(interaction.messages.toMutableList(), interaction.position)
                    adapter.stopLoading(interaction.messages.isEmpty())
                    when {
                        firstLoad -> {
                            var unreadPos = adapter.itemCount - 1 // default last item
                            for (index in interaction.messages.indices) {
                                val message = interaction.messages[index].message
                                if (!message.read && !message.isOut()) {
                                    unreadPos = index
                                    break
                                }
                            }
                            binding.rvChatList.scrollToPosition(unreadPos)
                        }
                        isAtEnd -> {
                            binding.rvChatList.scrollToPosition(adapter.itemCount - 1)
                        }
                    }
                }
                Interaction.Type.UPDATE -> {
                    adapter.update(interaction.messages, interaction.position)
                }
                Interaction.Type.REMOVE -> {
                    adapter.removeAt(interaction.position)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            adapter.update(viewModel.getStoredMessages())
            binding.rvChatList.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun loadMore(offset: Int) {
        viewModel.loadMessages(offset)
    }

    private fun initRecyclerView() {
        binding.rvChatList.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }
        binding.rvChatList.adapter = adapter
        binding.rvChatList.itemAnimator = null

        binding.rvChatList.addOnScrollListener(dateScroller)
        adapter.multiSelectListener = ::onMultiSelectChanged

        binding.fabHasMore.setOnClickListener { binding.rvChatList.scrollToPosition(adapter.itemCount - 1) }
        binding.rvChatList.addOnScrollListener(listScrollListener)
    }

    override fun onDestroyView() {
        _binding?.rvChatList?.removeOnScrollListener(dateScroller)
        _binding?.rvChatList?.removeOnScrollListener(listScrollListener)
        dateScroller.dispose()
        super.onDestroyView()
    }

    private fun onMultiSelectChanged(selectedCount: Int) {
        val binding = _binding ?: return
        binding.multiSelect.rlMultiAction.setVisible(selectedCount > 0)
        if (selectedCount == 0 && adapter.multiSelectMode) {
            adapter.multiSelectMode = false
        }
        binding.multiSelect.tvSelectedCount.text = context?.resources
                ?.getQuantityString(R.plurals.messages, selectedCount, selectedCount)
    }

    private fun initMultiAction() {
        binding.multiSelect.ivCancelMulti.setOnClickListener {
            adapter.multiSelectMode = false
            binding.multiSelect.rlMultiAction.hide()
        }
        binding.multiSelect.ivForwardMulti.setOnClickListener {
            val messageIds = getSelectedMessageIds()
            adapter.multiSelectMode = false
            startFragment<DialogsForwardFragment>(
                    DialogsForwardFragment.createArgs(forwarded = messageIds)
            )
        }
    }

    private inner class ListScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val binding = _binding ?: return
            if (binding.fabHasMore.visibility != View.VISIBLE &&
                    adapter.lastVisiblePosition(binding.rvChatList.layoutManager) != adapter.itemCount - 1) {
                binding.fabHasMore.show()
                onScrolled(isAtBottom = false)
            } else if (binding.fabHasMore.visibility != View.INVISIBLE
                    && adapter.lastVisiblePosition(binding.rvChatList.layoutManager) == adapter.itemCount - 1) {
                binding.fabHasMore.hide()
                onScrolled(isAtBottom = true)
            }
        }
    }

    private inner class RecyclerDateScroller : RecyclerView.OnScrollListener() {

        private var lastHandledTopPosition = -1
        private var lastHandledBottomPosition = -1
        private var disposable: Disposable? = null

        fun dispose() {
            disposable?.dispose()
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val adapterTopPosition = (recyclerView.layoutManager as? LinearLayoutManager)
                    ?.findFirstVisibleItemPosition() ?: -1
            val adapterBottomPosition = (recyclerView.layoutManager as? LinearLayoutManager)
                    ?.findLastVisibleItemPosition() ?: -1
            if (adapterTopPosition != lastHandledTopPosition
                    && adapterTopPosition != -1) {
                val message = adapter.items
                        .getOrNull(adapterTopPosition)
                        ?.message ?: return
                if (message.date == 0) return

                val uiDate = getDate(message.date)
                showDate(uiDate)
                lastHandledTopPosition = adapterTopPosition
                lastHandledBottomPosition = adapterBottomPosition

                disposable?.dispose()
                disposable = Completable.timer(2L, TimeUnit.SECONDS)
                        .compose(applyCompletableSchedulers())
                        .subscribe {
                            hideDate()
                        }

            }
        }

        private fun showDate(date: String) {
            val binding = _binding ?: return
            if (!binding.tvDatePopup.isShown) {
                binding.tvDatePopup.fadeIn(200L)
                binding.tvDatePopup.show()
            }
            binding.tvDatePopup.text = date
        }

        private fun hideDate() {
            _binding?.tvDatePopup?.fadeOut(200L) {
                _binding?.tvDatePopup?.hide()
            }
        }
    }
}
