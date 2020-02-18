package com.foreknowledge.photomemo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.fragment_photo.view.*

class PhotoPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    val photoFragments = mutableListOf<PhotoFragment>()

    override fun getItem(position: Int): PhotoFragment = photoFragments[position]
    override fun getCount(): Int = photoFragments.size
}

class PhotoFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_photo, container, false)

        val imagePath = arguments?.getString(KeyName.IMAGE_PATH)
        val imageBitmap = BitmapFactory.decodeFile(imagePath)
        rootView.photo_view.setImageBitmap(imageBitmap)

        return rootView
    }
}