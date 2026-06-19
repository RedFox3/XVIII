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

package com.twoeightnine.root.xvii.uikit

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseActivity
import com.twoeightnine.root.xvii.databinding.Toolbar2Binding
import com.twoeightnine.root.xvii.databinding.ViewTabsBinding
import global.msnthrp.xvii.uikit.extensions.EndAnimatorListener
import global.msnthrp.xvii.uikit.extensions.applyTopInsetPadding
import global.msnthrp.xvii.uikit.extensions.setVisible

class XviiToolbar(context: Context, attributeSet: AttributeSet) : AppBarLayout(context, attributeSet) {

    val binding = Toolbar2Binding.inflate(LayoutInflater.from(context), this, true)

    var title: String? = null
        set(value) {
            field = value
            binding.tvToolbarTitle.text = value
        }

    var forChat: Boolean = false
        set(value) {
            field = value
            binding.rlChat.setVisible(value)
            binding.tvToolbarTitle.text = if (value) "" else title
        }

    var showLogo: Boolean = false
        set(value) {
            field = value
            setLogoVisible(value)
        }

    private var hasBackArrow: Boolean = true
    private var withTabs: Boolean = false
    private var alwaysLifted: Boolean = false


    private var tabsBinding: ViewTabsBinding? = null

    private var animationRunning = false

//    var isLifted: Boolean
//        get() = elevation != 0f
//        set(value) {
//            updateElevation(if (value) 10f else 0f)
//        }

    var onClick: (() -> Unit)? = null
        set(value) {
            field = value
            binding.toolbar.setOnClickListener { value?.invoke() }
        }

    init {
        initAttributes(attributeSet)
        isLiftOnScroll = !alwaysLifted
        setBackgroundColor(ContextCompat.getColor(context, R.color.background))
        binding.toolbar.title = ""
        if (!forChat) {
            binding.tvToolbarTitle.text = title
        }
        binding.toolbar.overflowIcon?.paint(Munch.color.color)

        if (withTabs) {
            addTabs()
        }
        binding.rlChat.setVisible(forChat)
        binding.tvSubtitle.isSelected = true
        setLogoVisible(showLogo)

        applyTopInsetPadding()
    }

    fun showOverflowMenu() {
        binding.toolbar.showOverflowMenu()
    }

    private fun initAttributes(attributeSet: AttributeSet) {
        val attrs = context.theme.obtainStyledAttributes(attributeSet, R.styleable.XviiToolbar, 0, 0)
        title = attrs.getString(R.styleable.XviiToolbar_title)
        hasBackArrow = attrs.getBoolean(R.styleable.XviiToolbar_backArrow, true)
        withTabs = attrs.getBoolean(R.styleable.XviiToolbar_withTabs, false)
        forChat = attrs.getBoolean(R.styleable.XviiToolbar_forChat, false)
        alwaysLifted = attrs.getBoolean(R.styleable.XviiToolbar_alwaysLifted, false)
        showLogo = attrs.getBoolean(R.styleable.XviiToolbar_showLogo, false)
        attrs.recycle()
    }

    private fun addTabs() {
        tabsBinding = ViewTabsBinding.inflate(LayoutInflater.from(context)).apply {
            root.tabTextColors = ColorStateList(
                    arrayOf(
                            intArrayOf(android.R.attr.state_selected),
                            intArrayOf()
                    ),
                    intArrayOf(
                            Munch.color.color,
                            ContextCompat.getColor(context, R.color.minor_text)
                    )
            )
            root.setSelectedTabIndicatorColor(Munch.color.color)
            this@XviiToolbar.addView(root)
        }
    }

    fun setupWith(baseActivity: BaseActivity) {
        baseActivity.setSupportActionBar(binding.toolbar)
        baseActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(hasBackArrow)
            val homeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_back)
            homeDrawable?.paint(Munch.color.color)
            setHomeAsUpIndicator(homeDrawable)
            setHomeButtonEnabled(true)
            setDisplayUseLogoEnabled(false)
        }
    }

    fun setupWith(viewPager: ViewPager) {
        tabsBinding?.tabs?.setupWithViewPager(viewPager, true)
    }

    private fun updateElevation(newElevation: Float) = synchronized(this) {
        val currentElevation = elevation
        if (animationRunning || newElevation == currentElevation) return@synchronized

        ValueAnimator.ofFloat(currentElevation, newElevation).apply {
            duration = LIFT_ANIM_DURATION
            addUpdateListener { animation ->
                elevation = animation.animatedValue as Float
            }
            addListener(EndAnimatorListener {
                animationRunning = false
            })
            start()
            animationRunning = true
        }
    }

    private fun setLogoVisible(visible: Boolean) {
        binding.ivToolbarLogo.setVisible(visible)
        binding.tvToolbarTitle.text = if (visible) "" else title
        if (visible) {
            binding.ivToolbarLogo.paint(ContextCompat.getColor(context, R.color.main_text))
        }
    }

    companion object {
        private const val LIFT_ANIM_DURATION = 120L
    }
}
