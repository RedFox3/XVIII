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

package com.twoeightnine.root.xvii.dialogs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.twoeightnine.root.xvii.App
import com.twoeightnine.root.xvii.BuildConfig
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseFragment
import com.twoeightnine.root.xvii.chats.messages.chat.secret.SecretChatActivity
import com.twoeightnine.root.xvii.chats.messages.chat.usual.ChatActivity
import com.twoeightnine.root.xvii.databinding.FragmentDialogsBinding
import com.twoeightnine.root.xvii.dialogs.adapters.DialogsAdapter
import com.twoeightnine.root.xvii.dialogs.viewmodels.DialogsViewModel
import com.twoeightnine.root.xvii.model.Wrapper
import com.twoeightnine.root.xvii.utils.AppBarLifter
import com.twoeightnine.root.xvii.utils.FakeData
import com.twoeightnine.root.xvii.utils.LegalAlertDialog
import com.twoeightnine.root.xvii.utils.ShortcutUtils
import com.twoeightnine.root.xvii.utils.contextpopup.ContextPopupItem
import com.twoeightnine.root.xvii.utils.contextpopup.createContextPopup
import com.twoeightnine.root.xvii.utils.matchesUserId
import com.twoeightnine.root.xvii.utils.notifications.NotificationUtils
import com.twoeightnine.root.xvii.utils.showDeleteDialog
import com.twoeightnine.root.xvii.utils.showError
import com.twoeightnine.root.xvii.utils.showToast
import com.twoeightnine.root.xvii.views.TextInputAlertDialog
import global.msnthrp.xvii.data.dialogs.Dialog
import global.msnthrp.xvii.uikit.extensions.applyBottomInsetPadding
import global.msnthrp.xvii.uikit.extensions.hide
import global.msnthrp.xvii.uikit.extensions.show
import javax.inject.Inject


open class DialogsFragment : BaseFragment<FragmentDialogsBinding>() {

    @Inject
    lateinit var viewModelFactory: DialogsViewModel.Factory
    private lateinit var viewModel: DialogsViewModel

    private val adapter by lazy {
        DialogsAdapter(requireContext(), ::loadMore, ::onClick, ::onLongClick)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDialogsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()

        binding.apply {
            progressBar.show()
            swipeRefresh.setOnRefreshListener {
                viewModel.loadDialogs()
                adapter.reset()
                adapter.startLoading()
            }
            rvDialogs.applyBottomInsetPadding()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        App.appComponent?.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[DialogsViewModel::class.java]
        viewModel.getDialogs().observe(viewLifecycleOwner, Observer(::updateDialogs))
        viewModel.getTypingPeerIds().observe(viewLifecycleOwner) { adapter.typingPeerIds = it }
        viewModel.loadDialogs()
        adapter.startLoading()

        LegalAlertDialog(requireContext()).showIfNotAccepted()
    }

    private fun initRecycler() {
        binding.apply {
            rvDialogs.layoutManager = LinearLayoutManager(context)
            rvDialogs.adapter = adapter
            rvDialogs.addOnScrollListener(AppBarLifter(xviiToolbar))
        }
    }

    private fun updateDialogs(data: Wrapper<ArrayList<Dialog>>) {
        binding.apply {
            swipeRefresh.isRefreshing = false
            progressBar.hide()
        }
        if (data.data != null) {
            if (FakeData.ENABLED_DIALOGS) {
                adapter.update(FakeData.dialogs)
            } else {
                adapter.update(data.data)
            }
        } else {
            showError(context, data.error ?: getString(R.string.error))
        }
    }

    private fun loadMore(offset: Int) {
        viewModel.loadDialogs(offset)
    }

    protected open fun onClick(dialog: Dialog) {
        ChatActivity.launch(context, dialog)
    }

    protected open fun onLongClick(dialog: Dialog) {
        val items = arrayListOf(
                ContextPopupItem(
                        if (dialog.isPinned) R.drawable.ic_pinned_crossed else R.drawable.ic_pinned,
                        if (dialog.isPinned) R.string.unpin else R.string.pin
                ) { viewModel.pinDialog(dialog) },
                ContextPopupItem(R.drawable.ic_eye, R.string.mark_as_read) {
                    viewModel.readDialog(dialog)
                },
                ContextPopupItem(R.drawable.ic_alias, R.string.alias) {
                    TextInputAlertDialog(
                            requireContext(),
                            dialog.title,
                            dialog.aliasOrTitle
                    ) { newAlias ->
                        viewModel.addAlias(dialog, newAlias)
                    }.show()
                },
                ContextPopupItem(R.drawable.ic_link, R.string.add_shortcut) {
                    context?.also { context ->
                        ShortcutUtils.createShortcut(context, dialog) {
                            showToast(context, "shortcut added")
                        }
                    }
                }
        )

        if (dialog.peerId.matchesUserId()) {
            items.add(ContextPopupItem(R.drawable.ic_start_secret_chat, R.string.encryption) {
                SecretChatActivity.launch(context, dialog)
            })
        }

        if (BuildConfig.DEBUG) {
            items.add(ContextPopupItem(R.drawable.ic_source_code, R.string.notifications) {
                NotificationUtils.showTestMessageNotification(requireContext(), dialog)
            })
        }

        items.add(ContextPopupItem(R.drawable.ic_delete_popup, R.string.delete) {
            showDeleteDialog(context, getString(R.string.this_dialog)) {
                viewModel.deleteDialog(dialog)
            }
        })

        createContextPopup(context ?: return, items).show()
    }

    companion object {
        fun newInstance() = DialogsFragment()
    }
}