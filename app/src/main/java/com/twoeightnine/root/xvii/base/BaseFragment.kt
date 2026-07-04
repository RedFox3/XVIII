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

package com.twoeightnine.root.xvii.base

import android.os.Bundle
import android.view.*
import androidx.annotation.MenuRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.lg.L
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.XviiToolbar
import com.twoeightnine.root.xvii.uikit.paint

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected var _binding: VB? = null
    protected val binding get() = _binding!!

    private val baseActivity
        get() = activity as? BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        l("onViewCreated")
        ViewCompat.requestApplyInsets(view)

        view.findViewById<XviiToolbar>(R.id.xviiToolbar)?.apply {
            baseActivity?.also(::setupWith)
        }
    }

    override fun onResume() {
        super.onResume()
        l("onResume")
    }

    override fun onPause() {
        l("onPause")
        super.onPause()
    }

    override fun onDestroyView() {
        l("onDestroyView")
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuId = getMenu()
        if (menuId != 0) {
            inflater.inflate(menuId, menu)
            menu.paint(Munch.color.color)
        }
    }

    protected fun onBackPressed() {
        baseActivity?.onBackPressed()
    }

    @MenuRes
    protected open fun getMenu(): Int = 0

    protected fun setStatusBarLight(isLight: Boolean) {
        baseActivity?.window?.decorView?.apply {
            systemUiVisibility = if (isLight) {
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    protected fun <T> LiveData<T>.observe(observer: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer(observer))
    }

    private fun l(event: String) {
        L.tag("lifecycle").log("${javaClass.simpleName} $event")
    }
}