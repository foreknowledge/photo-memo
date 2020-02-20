package com.foreknowledge.photomemo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.github.chrisbanes.photoview.PhotoView

class PhotoPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    val photoFragments = mutableListOf<PhotoFragment>()

    override fun getItem(position: Int): PhotoFragment = photoFragments[position]
    override fun getCount(): Int = photoFragments.size
}

class PhotoFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val photoView = PhotoView(context)

        val imagePath = arguments?.getString(KeyName.IMAGE_PATH)
        val imageBitmap = BitmapFactory.decodeFile(imagePath)
        photoView.setImageBitmap(imageBitmap)

        return photoView
    }
}