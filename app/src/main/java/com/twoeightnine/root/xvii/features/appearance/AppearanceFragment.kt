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

package com.twoeightnine.root.xvii.features.appearance

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseFragment
import com.twoeightnine.root.xvii.chats.attachments.gallery.GalleryFragment
import com.twoeightnine.root.xvii.databinding.FragmentAppearanceBinding
import com.twoeightnine.root.xvii.extensions.load
import com.twoeightnine.root.xvii.managers.Prefs
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.paint
import com.twoeightnine.root.xvii.utils.*
import com.twoeightnine.root.xvii.views.LoadingDialog
import global.msnthrp.xvii.uikit.R as CommonR
import global.msnthrp.xvii.uikit.extensions.applyBottomInsetPadding
import global.msnthrp.xvii.uikit.extensions.hide
import global.msnthrp.xvii.uikit.extensions.lowerIf
import global.msnthrp.xvii.uikit.extensions.setVisible
import global.msnthrp.xvii.uikit.utils.color.ColorUtils
import java.io.File

class AppearanceFragment : BaseFragment<FragmentAppearanceBinding>() {

    private val mainTextLight by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.main_text_light)
    }
    private val otherTextLight by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.other_text_light)
    }
    private val minorTextLight by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.minor_text_light)
    }
    private val mainTextDark by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.main_text_dark)
    }
    private val otherTextDark by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.other_text_dark)
    }
    private val minorTextDark by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.minor_text_dark)
    }
    private val backgroundLight by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.background_light)
    }
    private val backgroundDark by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.background_dark)
    }
    private val backgroundDarkLighter by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.background_dark_lighter)
    }
    private val messageBackgroundLight by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.background_message_light)
    }
    private val messageBackgroundDark by lazy {
        ContextCompat.getColor(requireContext(), CommonR.color.background_message_dark)
    }

    private lateinit var bottomSheetHelper: BottomSheetHelper
    private lateinit var permissionHelper: PermissionHelper

    private var isLightBefore = false
    private var colorBefore = 0
    private var currentColor = 0

    var dialog: LoadingDialog? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAppearanceBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorBefore = Prefs.color
        currentColor = colorBefore
        initViews()
        invalidateSample()
        binding.rlHideBottom.paint(Munch.color.color)
        binding.sample.input.pbAttach.hide()
        binding.sample.input.rlAttachCount.hide()

        binding.sample.input.etInput.isClickable = false
        binding.sample.input.etInput.isFocusable = false

        bottomSheetHelper = BottomSheetHelper(
                binding.rlBottom,
                binding.rlHideBottom,
                binding.tvBottomTitle,
                R.id.flBottom,
                childFragmentManager,
                resources.getDimensionPixelSize(R.dimen.bottomsheet_height)
        )
        permissionHelper = PermissionHelper(this)

        binding.svContent.applyBottomInsetPadding()
        binding.rlBottom.applyBottomInsetPadding()
    }

    private fun invalidateSample() {
        applyColors()
        applyTexts()
        applyVisibility()
    }

    private fun applyColors() {
        val color = Munch.ColorScope(currentColor)
        binding.csThemeColor.color = currentColor

        with(binding.sample) {
            arrayOf(input.ivMic, input.ivSend, ivBackSample, readStateDot).forEach { iv ->
                iv.drawable.paint(color.color)
            }

            if (binding.switchLightTheme.isChecked) {

                rlToolbar.setBackgroundColor(backgroundLight)
                rlSampleRoot.setBackgroundColor(backgroundLight)
                input.rlInputBack.setBackgroundColor(backgroundLight)

                arrayOf(tvTitle, tvBodyIn, tvBodyOut, input.etInput).forEach { it.setTextColor(mainTextLight) }
                arrayOf(tvDateIn, tvDateOut).forEach { it.setTextColor(otherTextLight) }
                tvSubtitle.setTextColor(minorTextLight)

                arrayOf(input.ivKeyboard, input.ivAttach).forEach { it.paint(color.colorWhite(50)) }
                (llMessageIn.background as? GradientDrawable)
                        ?.setColor(messageBackgroundLight)
                (llMessageOut.background as? GradientDrawable)
                        ?.setColor(color.color(Munch.UseCase.MESSAGES_OUT, Munch.Theme.WHITE))
            } else {

                rlToolbar.setBackgroundColor(backgroundDark)
                rlSampleRoot.setBackgroundColor(backgroundDark)
                input.rlInputBack.setBackgroundColor(backgroundDarkLighter)

                arrayOf(tvTitle, tvBodyIn, tvBodyOut, input.etInput).forEach { it.setTextColor(mainTextDark) }
                arrayOf(tvDateIn, tvDateOut).forEach { it.setTextColor(otherTextDark) }
                tvSubtitle.setTextColor(minorTextDark)

                arrayOf(input.ivKeyboard, input.ivAttach).forEach { it.paint(color.colorDark(50)) }
                (llMessageIn.background as? GradientDrawable)
                        ?.setColor(messageBackgroundDark)
                (llMessageOut.background as? GradientDrawable)
                        ?.setColor(color.color(Munch.UseCase.MESSAGES_OUT, Munch.Theme.DARK))
            }
        }
    }

    private fun applyTexts() {
        val context = context ?: return

        val useAppleEmojis = binding.switchAppleEmojis.isChecked
        val showSeconds = binding.switchShowSeconds.isChecked
        val inLower = binding.switchLowerTexts.isChecked

        val sampleIn = getString(R.string.appearance_sample_in)
        val sampleOut = getString(R.string.appearance_sample_out)
        val sampleDateIn = getTime(time() - 3647, withSeconds = showSeconds)
        val sampleDateOut = getTime(time() - 364, withSeconds = showSeconds)
        val sampleLastSeen = LastSeenUtils.getFull(
                context = context,
                isOnline = false,
                timeStamp = time() - 2147,
                deviceCode = 0,
                withSeconds = showSeconds
        )

        with(binding.sample) {
            tvBodyIn.text = when {
                useAppleEmojis -> EmojiHelper.getEmojied(context, sampleIn, ignorePref = true)
                else -> sampleIn
            }
            tvBodyOut.text = when {
                useAppleEmojis -> EmojiHelper.getEmojied(context, sampleOut, ignorePref = true)
                else -> sampleOut
            }

            tvBodyIn.setTextSize(TypedValue.COMPLEX_UNIT_SP, binding.stMessageSize.value.toFloat())
            tvBodyOut.setTextSize(TypedValue.COMPLEX_UNIT_SP, binding.stMessageSize.value.toFloat())
            input.etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, binding.stMessageSize.value.toFloat() + 2)

            tvDateIn.text = sampleDateIn
            tvDateOut.text = sampleDateOut
            tvSubtitle.text = sampleLastSeen

            tvTitle.text = getString(R.string.appearance_sample_name)
            input.etInput.setText(getString(R.string.appearance_sample_input))
            tvTitle.lowerIf(inLower)
            input.etInput.lowerIf(inLower)
        }
    }

    private fun applyVisibility() {
        val showVoice = binding.switchShowVoice.isChecked
        val showStickers = binding.switchShowStickers.isChecked

        with(binding.sample.input) {
            ivKeyboard.setVisible(showStickers)
            ivMic.setVisible(showVoice)
            ivSend.setVisible(!showVoice)
        }
    }

    private fun initViews() {
        isLightBefore = Prefs.isLightTheme
        binding.switchLightTheme.onCheckedListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            applyColors()
        }
        binding.switchLightTheme.isChecked = isLightBefore
        if (Prefs.chatBack.isNotEmpty()) {
            updatePhoto(Prefs.chatBack)
        }
        binding.btnGallery.setOnClickListener { openGallery() }
        binding.csThemeColor.setOnClickListener {
            showColorPicker(currentColor) { color ->
                currentColor = color
                applyColors()
            }
        }

        binding.switchChatBack.onCheckedListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            binding.llCustomBack.setVisible(isChecked)
            if (!isChecked) {
                deletePhoto()
            }
        }
        binding.switchChatBack.isChecked = Prefs.chatBack.isNotBlank()
        binding.llCustomBack.setVisible(binding.switchChatBack.isChecked)
        binding.btnColor.setOnClickListener {
            showColorPicker(currentColor, ::convertColor)
        }

        binding.switchShowSeconds.isChecked = Prefs.showSeconds
        binding.switchLowerTexts.isChecked = Prefs.lowerTexts
        binding.switchAppleEmojis.isChecked = Prefs.appleEmojis
        binding.switchShowStickers.isChecked = Prefs.showStickers
        binding.switchShowVoice.isChecked = Prefs.showVoice

        CompoundButton.OnCheckedChangeListener { _, _ ->
            applyTexts()
        }.apply {
            binding.switchAppleEmojis.onCheckedListener = this
            binding.switchLowerTexts.onCheckedListener = this
            binding.switchShowSeconds.onCheckedListener = this
        }

        CompoundButton.OnCheckedChangeListener { _, _ ->
            applyVisibility()
        }.apply {
            binding.switchShowStickers.onCheckedListener = this
            binding.switchShowVoice.onCheckedListener = this
        }

        binding.stMessageSize.value = Prefs.messageTextSize
        binding.stMessageSize.onValueChangedListener = { applyTexts() }
    }

    private fun openGallery() {
        permissionHelper.doOrRequest(
                arrayOf(PermissionHelper.READ_STORAGE, PermissionHelper.WRITE_STORAGE),
                R.string.no_access_to_storage,
                R.string.need_access_to_storage
        ) {
            bottomSheetHelper.openBottomSheet(GalleryFragment.newInstance(onlyPhotos = true) {
                bottomSheetHelper.closeBottomSheet()
                if (it.isNotEmpty()) {
                    convertPhoto(it[0].path)
                } else {
                    showError(activity, R.string.error)
                }
            }, getString(R.string.gallery))
        }
    }

    private fun deletePhoto() {
        binding.sample.ivBackground.setImageBitmap(null)
        deleteOldChatBack(Prefs.chatBack)
        Prefs.chatBack = ""
    }

    private fun convertPhoto(path: String) {
        val activity = activity ?: return

        val newPath = getCroppedImagePath(activity, path)
        if (newPath != null) {
            deleteOldChatBack(Prefs.chatBack)
            Prefs.chatBack = newPath
            updatePhoto(newPath)
        } else {
            showAlert(context, getString(R.string.unable_to_crop))
        }
    }

    private fun convertColor(color: Int) {
        val activity = activity ?: return

        val newPath = createColoredBitmap(activity, color)
        if (newPath != null) {
            deleteOldChatBack(Prefs.chatBack)
            Prefs.chatBack = newPath
            updatePhoto(newPath)
        } else {
            showAlert(context, getString(R.string.unable_to_pick_color))
        }
    }

    private fun updatePhoto(path: String) {
        binding.sample.ivBackground.load("file://$path")
    }

    private fun deleteOldChatBack(prevChatBackPath: String) {
        Thread {
            File(prevChatBackPath).delete()
        }.start()
    }

    override fun onStop() {
        super.onStop()
        GalleryFragment.clear()

        savePreferences()
    }

    /**
     * for parent activity
     */
    fun hasChanges() = isLightBefore != binding.switchLightTheme.isChecked
            || currentColor != colorBefore

    /**
     * for parent activity
     */
    fun askForRestarting() {
        showConfirm(context, getString(R.string.wanna_change_theme)) { yes ->
            if (yes) {
                Prefs.color = currentColor
                Prefs.colorBetterWithWhite = ColorUtils.isColorBetterWithWhite(currentColor)
                Prefs.isLightTheme = binding.switchLightTheme.isChecked
                savePreferences()
                restartApp(context, getString(R.string.theme_changed))
            } else {
                binding.switchLightTheme.isChecked = isLightBefore
                currentColor = colorBefore
                activity?.onBackPressed()
            }
        }
    }

    private fun showColorPicker(initColor: Int, onPicked: (Int) -> Unit) {
        ColorPickerDialogBuilder.with(context)
                .initialColor(initColor)
                .lightnessSliderOnly()
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton(R.string.ok) { _, color, _ ->
                    onPicked(color)
                }
                .setNegativeButton(R.string.cancel, null)
                .build()
                .apply { stylize() }
                .show()
    }

    private fun savePreferences() {
        Prefs.showSeconds = binding.switchShowSeconds.isChecked
        Prefs.lowerTexts = binding.switchLowerTexts.isChecked
        Prefs.appleEmojis = binding.switchAppleEmojis.isChecked
        Prefs.showStickers = binding.switchShowStickers.isChecked
        Prefs.showVoice = binding.switchShowVoice.isChecked
        Prefs.messageTextSize = binding.stMessageSize.value
    }

    companion object {

        fun newInstance() = AppearanceFragment()
    }
}
