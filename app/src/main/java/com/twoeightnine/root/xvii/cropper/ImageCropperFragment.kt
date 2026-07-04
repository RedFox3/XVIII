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

package com.twoeightnine.root.xvii.cropper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseFragment
import com.twoeightnine.root.xvii.databinding.FragmentCropperBinding
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.paint
import com.twoeightnine.root.xvii.utils.CacheFileUtils
import com.twoeightnine.root.xvii.utils.applySingleSchedulers
import com.twoeightnine.root.xvii.utils.saveBmp
import com.twoeightnine.root.xvii.utils.time
import global.msnthrp.xvii.uikit.extensions.hide
import global.msnthrp.xvii.uikit.extensions.show
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.io.File

class ImageCropperFragment : BaseFragment<FragmentCropperBinding>() {

    private val path by lazy {
        arguments?.getString(ARG_PATH)
    }

    private var cropperDisposable: Disposable? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCropperBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            cropImageView.setImageUriAsync("file://$path".toUri())
            ivDone.setOnClickListener {
                cropAndExit()
            }
            ivRotateCounter.setOnClickListener {
                cropImageView.rotateImage(-90)
            }
            ivRotateClock.setOnClickListener {
                cropImageView.rotateImage(90)
            }

            ivDone.paint(Munch.color.color)
            ViewCompat.setOnApplyWindowInsetsListener(llBottom) { v, insets ->
                val padding =
                    context?.resources?.getDimensionPixelSize(R.dimen.attach_fab_done_margin) ?: 0
                v.setPadding(0, padding, 0, padding + insets.systemWindowInsetBottom)
                insets
            }
        }
    }

    private fun cropAndExit() {
        binding.rvLoader.show()
        binding.llBottom.hide()
        cropperDisposable?.dispose()
        cropperDisposable = Single.fromCallable {
            val croppedImage = binding.cropImageView.croppedImage
            val cacheDir = requireContext().cacheDir

            val croppedDir = File(cacheDir, CacheFileUtils.DIR_CROPPED)
            croppedDir.mkdir()

            val croppedFileName = File(croppedDir, "cropped_${time()}.png")
            saveBmp(croppedFileName.absolutePath, croppedImage!!) // TODO: check if it works
            croppedFileName.absolutePath
        }
                .compose(applySingleSchedulers())
                .onErrorReturnItem("")
                .subscribe(::onImageCropped)
    }

    private fun onImageCropped(croppedPath: String) {
        val intent = Intent().apply {
            putExtra(RESULT_ORIG_PATH, path)
            putExtra(RESULT_CROPPED_PATH, croppedPath)
        }
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun onDestroy() {
        cropperDisposable?.dispose()
        super.onDestroy()
    }

    companion object {

        const val RESULT_ORIG_PATH = "orig_path"
        const val RESULT_CROPPED_PATH = "cropped_path"

        private const val ARG_PATH = "path"

        fun createArgs(path: String) = Bundle().apply {
            putString(ARG_PATH, path)
        }
    }
}