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

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseReachAdapter
import com.twoeightnine.root.xvii.base.FragmentPlacementActivity.Companion.startFragment
import com.twoeightnine.root.xvii.chats.attachments.AttachmentsInflater
import com.twoeightnine.root.xvii.chats.messages.deepforwarded.DeepForwardedFragment
import com.twoeightnine.root.xvii.databinding.ItemMessageInChatBinding
import com.twoeightnine.root.xvii.databinding.ItemMessageRepliedBinding
import com.twoeightnine.root.xvii.extensions.getInitials
import com.twoeightnine.root.xvii.managers.Prefs
import com.twoeightnine.root.xvii.model.attachments.getAudios
import com.twoeightnine.root.xvii.model.messages.Message
import com.twoeightnine.root.xvii.model.messages.WrappedMessage
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.XviiAvatar
import com.twoeightnine.root.xvii.uikit.paint
import com.twoeightnine.root.xvii.utils.*
import global.msnthrp.xvii.uikit.extensions.*

/**
 * definitely it waits for refactoring
 */
class MessagesAdapter(context: Context,
                      loader: (Int) -> Unit,
                      private val messageCallback: Callback,
                      private val attachmentsCallback: AttachmentsInflater.Callback,
                      private val settings: Settings
) : BaseReachAdapter<WrappedMessage, MessagesAdapter.MessageViewHolder>(context, loader) {

    private val messageInflater = AttachmentsInflater(context, attachmentsCallback)

    private val messageTextSize by lazy {
        Prefs.messageTextSize.toFloat()
    }

    private val textWidthInlineFitness by lazy {
        context.resources.getDimensionPixelSize(R.dimen.chat_message_inline_fitness_width)
    }
    private val dateTextExtraPadding by lazy {
        context.resources.getDimensionPixelSize(R.dimen.chat_date_text_margin_end)
    }
    private val dateTextMarginTop by lazy {
        context.resources.getDimensionPixelSize(R.dimen.chat_date_text_margin_top)
    }
    private val dateTextMarginSingle by lazy {
        context.resources.getDimensionPixelSize(R.dimen.chat_date_text_margin_single)
    }
    private val levelPadding by lazy {
        context.resources.getDimensionPixelSize(R.dimen.chat_message_level_padding)
    }
    private val messageBackground by lazy {
        ContextCompat.getColor(context, R.color.message_background_gray)
    }

    init {
        messageInflater.audiosFetcher = {
            items.mapNotNull { it.message.attachments }
                    .flatten()
                    .getAudios()
                    .filterNotNull()
        }
        messageInflater.audioMessagesFetcher = {
            items.flatMap { it.message.getAllAudioMessages() }
        }
    }

    override fun createHolder(parent: ViewGroup, viewType: Int) = MessageViewHolder(inflater.inflate(
            when (viewType) {

                // outgoing. one for all types of chats
                OUT -> R.layout.item_message_out

                // incoming in conversations: with avatars and names
                IN_CHAT -> R.layout.item_message_in_chat

                // incoming in personal chats: no avatars and names
                IN_USER -> R.layout.item_message_in_user

                // system messages. one for all types
                SYSTEM -> R.layout.item_message_system

                // unreachable branch
                else -> R.layout.item_message_in_chat
            }, parent, false))

    override fun bind(holder: MessageViewHolder, item: WrappedMessage) {
        val position = items.indexOf(item)
        holder.bind(item, items.getOrNull(position - 1))
    }

    override fun createStubLoadItem() = WrappedMessage(Message())

    override fun getItemViewType(position: Int): Int {
        val message = items[position].message
        val superType = super.getItemViewType(position)
        return when {
            superType != NO_STUB -> superType
            message.isSystem() -> SYSTEM
            message.isOut() -> OUT
            message.isChat() || settings.isImportant -> IN_CHAT
            else -> IN_USER
        }
    }

    // TODO: Refactor ViewHolder
    inner class MessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private val rootViews = MessageViews(itemView)

        fun bind(wrappedMessage: WrappedMessage, prevWrappedMessage: WrappedMessage?, level: Int = 0) {
            val message = wrappedMessage.message

            if (message.isSystem()) {
                bindSystemMessage(rootViews, message)
            } else {
                val isOutgoingStack = wrappedMessage.message.isOut() || !wrappedMessage.sent
                bindRegularMessage(rootViews, wrappedMessage, prevWrappedMessage, level, isOutgoingStack)
                setupListeners(rootViews)
            }
        }

        private fun setupListeners(views: MessageViews) {
            with(views) {
                val clickListener = View.OnClickListener {
                    items.getOrNull(adapterPosition)?.also(::onClick)
                }
                val longClickListener = View.OnLongClickListener {
                    items.getOrNull(adapterPosition)?.let(::onLongClick) == true
                }
                rlBack?.setOnClickListener(clickListener)
                rlBack?.setOnLongClickListener(longClickListener)
                tvBody?.setOnClickListener(clickListener)
                tvBody?.setOnLongClickListener(longClickListener)
            }
        }

        private fun onClick(message: WrappedMessage) {
            if (multiSelectMode) {
                multiSelect(message)
                invalidateBackground(message, rootViews.rlBack, 0)
            } else {
                messageCallback.onClicked(message.message)
            }
        }

        private fun onLongClick(message: WrappedMessage): Boolean {
            if (!multiSelectMode) {
                multiSelectMode = true
                multiSelect(message)
                invalidateBackground(message, rootViews.rlBack, 0)
                return true
            }
            return false
        }

        private fun invalidateBackground(message: WrappedMessage, rlBack: View?, level: Int) {
            rlBack?.setBackgroundColor(if (level == 0 && message in multiSelect) {
                ContextCompat.getColor(context, R.color.selected_mess)
            } else {
                Color.TRANSPARENT
            })
        }

        private fun bindSystemMessage(views: MessageViews, message: Message) {
            views.tvSystem?.apply {
                text = message.action?.getSystemMessage(context)
                val userId = message.action?.memberId ?: message.fromId
                setOnClickListener { messageCallback.onUserClicked(userId) }
            }
        }

        private fun bindRegularMessage(
                views: MessageViews,
                wrappedMessage: WrappedMessage,
                prevWrappedMessage: WrappedMessage?,
                level: Int,
                isOutgoingStack: Boolean
        ) {
            val message = wrappedMessage.message
            val prevMessage = prevWrappedMessage?.message
            val isNotSent = !wrappedMessage.sent

            with(views) {
                invalidateBackground(wrappedMessage, rlBack, level)

                tvBody?.also { bindMessageText(it, message.text) }
                bindMessageDate(rlDateSeparator, tvDateSeparator, message, prevMessage, level)
                bindMessageTime(context, message, level, tvBody, tvDateText, tvDateTextInlined,
                        tvDateAttachmentsOverlay, tvDateAttachmentsEmbedded)
                bindName(rlName, tvName, civPhoto, message, prevMessage)

                ivSendingIcon?.apply {
                    setVisible(isNotSent)
                    paint(Munch.color.color)
                }

                ivReadDot?.apply {
                    paint(Munch.color.color)
                    setVisibleWithInvis(!message.read && message.isOut() && !isNotSent)
                }

                val paintDelta = if (isOutgoingStack) 1 else 0
                llMessage?.stylizeAsMessage(
                        level + paintDelta,
                        hide = message.run { isSticker() || isGraffiti() || isGift() }
                )
                llMessage?.layoutParams?.width = messageInflater.getMessageWidth(message, settings.fullDeepness, level)

                bindContent(views, wrappedMessage, level, isOutgoingStack)
            }
        }

        private fun bindContent(
                views: MessageViews,
                wrappedMessage: WrappedMessage,
                level: Int,
                isOutgoingStack: Boolean
        ) {
            val message = wrappedMessage.message
            val hasAttachments = !message.attachments.isNullOrEmpty()
            val hasForwarded = !message.fwdMessages.isNullOrEmpty()
            val hasReplied = message.replyMessage != null
            val isNotSent = !wrappedMessage.sent
            val hasAttachmentsOrForwarded = wrappedMessage.hasAttachmentsOrForwarded
            val paintDelta = if (isOutgoingStack) 1 else 0

            views.llMessageContainer?.apply {
                removeAllViews()
                val hasContent = hasAttachments || hasForwarded || hasReplied || (isNotSent && hasAttachmentsOrForwarded)
                setVisible(hasContent)

                if (isNotSent && hasAttachmentsOrForwarded) {
                    addView(messageInflater.getViewLoader())
                }

                message.replyMessage?.let { reply ->
                    val replyView = messageInflater.getRepliedMessageView(reply)
                    addView(replyView)
                    ItemMessageRepliedBinding.bind(replyView).llRepliedMessage
                            .stylizeAsMessage(level + paintDelta + 1)
                }

                if (hasAttachments) {
                    messageInflater.createViewsFor(message, level).forEach(::addView)
                }

                if (hasForwarded) {
                    views.rlBack?.apply { setPadding(paddingLeft, paddingTop, 6, paddingBottom) }
                    bindForwardedMessages(this, message, level, isOutgoingStack)
                }
            }
        }

        private fun bindForwardedMessages(
                container: ViewGroup,
                message: Message,
                level: Int,
                isOutgoingStack: Boolean
        ) {
            message.fwdMessages?.forEachIndexed { index, innerMessage ->
                val binding = ItemMessageInChatBinding.inflate(inflater, container, false)
                val included = binding.root
                val maxWidth = messageInflater.getMessageMaxWidth(settings.fullDeepness, level + 1)
                (binding.llMessage.layoutParams as? ConstraintLayout.LayoutParams)
                        ?.matchConstraintMaxWidth = maxWidth
                binding.rlBack.setPadding(binding.rlBack.paddingLeft, binding.rlBack.paddingTop, 6, binding.rlBack.paddingBottom)

                if (level < ALLOWED_DEEPNESS || settings.fullDeepness) {
                    val wrappedInnerMessage = WrappedMessage(innerMessage)
                    val wrappedPrevInnerMessage = message.fwdMessages.getOrNull(index - 1)?.let(::WrappedMessage)
                    bindRegularMessage(MessageViews(included), wrappedInnerMessage, wrappedPrevInnerMessage, level + 1, isOutgoingStack)
                } else {
                    bindTooDeepForwarding(binding)
                }
                container.addView(included)
            }
        }

        private fun bindTooDeepForwarding(binding: ItemMessageInChatBinding) {
            with(binding) {
                tvBody.text = context.resources.getString(R.string.too_deep_forwarding)
                tvBody.paint(Munch.color.color)
                tvBody.paintFlags = tvBody.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                rlName.hide()
                root.setOnClickListener {
                    val messageId = items.getOrNull(adapterPosition)?.message?.id ?: return@setOnClickListener
                    context.startFragment<DeepForwardedFragment>(DeepForwardedFragment.createArgs(messageId))
                }
            }
        }

        private fun bindMessageText(textView: TextView, messageText: String) {
            val isNotEmpty = messageText.isNotEmpty()
            textView.setVisible(isNotEmpty)

            if (isNotEmpty) {
                val preparedText = wrapMentions(context, messageText, addClickable = true)
                textView.text = when {
                    EmojiHelper.hasEmojis(messageText) -> EmojiHelper.getEmojied(context, messageText, preparedText)
                    else -> preparedText

                }

                // TODO move to one-time setup
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, messageTextSize)
            }
        }

        private fun bindMessageDate(
                rlDateSeparator: View?,
                tvDateSeparator: TextView?,
                message: Message,
                prevMessage: Message?,
                level: Int
        ) {
            val dateOnlyDay = getDate(message.date)
            val dateOnlyDayPrev = prevMessage?.date?.let(::getDate)

            val zeroLevel = level == 0
            val dateChanged = dateOnlyDayPrev == null || dateOnlyDayPrev != dateOnlyDay

            rlDateSeparator?.setVisible(dateChanged && zeroLevel)
            if (rlDateSeparator?.isVisible() == true) {
                tvDateSeparator?.text = dateOnlyDay
            }
        }

        private fun bindMessageTime(
                context: Context,
                message: Message,
                level: Int,
                tvBody: TextView?,
                tvDateText: TextView?,
                tvDateTextInlined: TextView?,
                tvDateAttachmentsOverlay: TextView?,
                tvDateAttachmentsEmbedded: TextView?
        ) {
            val dateOnlyTime = getTime(message.date, noDate = true, withSeconds = Prefs.showSeconds)

            val edited = when {
                message.isEdited() -> context.resources.getString(R.string.edited)
                else -> ""
            }
            val dateMessage = "$dateOnlyTime $edited"

            val dateToBeShown = when (messageInflater.getTimeStyle(message)) {

                AttachmentsInflater.TimeStyle.ATTACHMENTS_OVERLAYED -> {
                    tvDateAttachmentsOverlay
                }

                AttachmentsInflater.TimeStyle.ATTACHMENTS_EMBEDDED -> {
                    tvDateAttachmentsEmbedded
                }

                AttachmentsInflater.TimeStyle.TEXT -> {
                    val bodyWidth = tvBody?.paint?.measureText(message.text) ?: 0f
                    val timeWidth = tvDateText?.paint?.measureText(dateMessage) ?: 0f

                    val isEmpty = message.text.isEmpty()
                    val freeSpace = textWidthInlineFitness - bodyWidth - 2 * levelPadding * level
                    val hasEnoughSpaceToInline = freeSpace > timeWidth + dateTextExtraPadding
                    val canBeInlined = hasEnoughSpaceToInline && !isEmpty

                    if (!canBeInlined) {
                        (tvDateText?.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                            if (isEmpty) {
                                setMargins(dateTextMarginSingle)
                                removeRule(RelativeLayout.ALIGN_END)
                                addRule(RelativeLayout.ALIGN_PARENT_END)
                            } else {
                                setMargins(0, dateTextMarginTop, 0, 0)
                                removeRule(RelativeLayout.ALIGN_PARENT_END)
                                addRule(RelativeLayout.ALIGN_END, R.id.tvBody)
                            }
                        }
                    }

                    when {
                        canBeInlined -> tvDateTextInlined
                        else -> tvDateText
                    }
                }
            }
            dateToBeShown?.apply {
                text = dateMessage
                show()
            }
            listOf(tvDateAttachmentsOverlay,
                    tvDateAttachmentsEmbedded,
                    tvDateTextInlined,
                    tvDateText)
                    .filter { it != dateToBeShown }
                    .forEach { it?.hide() }
        }

        private fun bindName(
                rlName: View?,
                tvName: TextView?,
                civPhoto: XviiAvatar?,
                message: Message,
                prevMessage: Message?
        ) {
            val showName = shouldShowName(message, prevMessage)
            rlName?.setVisible(showName)
            if (showName) {
                tvName?.apply {
                    text = message.name
                    lowerIf(Prefs.lowerTexts)
                }
                civPhoto?.apply {
                    load(message.photo, message.name?.getInitials(), id = message.fromId)
                }
                rlName?.setOnClickListener {
                    items.getOrNull(adapterPosition)
                            ?.message
                            ?.fromId
                            ?.also(messageCallback::onUserClicked)
                }
            }
        }

        private fun shouldShowName(message: Message, prevMessage: Message?) =
                // this message is first (no previous)
                prevMessage == null

                        // OR from different users
                        || message.fromId != prevMessage.fromId

                        // OR previous contains action
                        || prevMessage.isSystem()

                        // OR there are 2 hours between messages
                        || message.date - prevMessage.date > MESSAGES_BETWEEN_DELAY

        private fun ViewGroup.stylizeAsMessage(level: Int, hide: Boolean = false) {
            (background as GradientDrawable).setColor(
                    when {
                        hide -> Color.TRANSPARENT
                        level % 2 == 0 -> messageBackground
                        else -> Munch.color.color(Munch.UseCase.MESSAGES_OUT)
                    })
        }
    }

    private class MessageViews(root: View) {
        val tvBody: TextView? = root.findViewById(R.id.tvBody)
        val rlBack: View? = root.findViewById(R.id.rlBack)
        val llMessage: ViewGroup? = root.findViewById(R.id.llMessage)
        val llMessageContainer: ViewGroup? = root.findViewById(R.id.llMessageContainer)
        val tvDateSeparator: TextView? = root.findViewById(R.id.tvDateSeparator)
        val rlDateSeparator: View? = root.findViewById(R.id.rlDateSeparator)
        val tvDateAttachmentsOverlay: TextView? = root.findViewById(R.id.tvDateAttachmentsOverlay)
        val tvDateAttachmentsEmbedded: TextView? = root.findViewById(R.id.tvDateAttachmentsEmbedded)
        val tvDateTextInlined: TextView? = root.findViewById(R.id.tvDateTextInlined)
        val tvDateText: TextView? = root.findViewById(R.id.tvDateText)
        val rlName: View? = root.findViewById(R.id.rlName)
        val tvName: TextView? = root.findViewById(R.id.tvName)
        val civPhoto: XviiAvatar? = root.findViewById(R.id.civPhoto)
        val ivSendingIcon: ImageView? = root.findViewById(R.id.ivSendingIcon)
        val ivReadDot: ImageView? = root.findViewById(R.id.ivReadDot)
        val tvSystem: TextView? = root.findViewById(R.id.tvSystem)
    }


    interface Callback {
        fun onClicked(message: Message)
        fun onUserClicked(userId: Int)
    }

    data class Settings(
            val isImportant: Boolean,

            /**
             * if true forwarded messages shown as is. used in [DeepForwardedFragment]
             */
            val fullDeepness: Boolean = false
    )

    companion object {

        const val MESSAGES_BETWEEN_DELAY = 60 * 60 * 2 // 2 hours
        const val ALLOWED_DEEPNESS = 2

        const val OUT = 0
        const val IN_CHAT = 1
        const val IN_USER = 2
        const val SYSTEM = 3
    }
}
