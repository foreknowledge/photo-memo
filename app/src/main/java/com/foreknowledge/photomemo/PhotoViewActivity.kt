package com.foreknowledge.photomemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_photo_view.*

class PhotoViewActivity : AppCompatActivity() {
    private var totalImageCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        val photoFragments = mutableListOf<PhotoFragment>()

        val imagePaths = MemoDbTable(this).readImagePaths(intent.getLongExtra(KeyName.MEMO_ID, 0))
        totalImageCount = imagePaths.size

        for (path in imagePaths) {
            val bundle = Bundle()
            bundle.putString(KeyName.IMAGE_PATH, path)

            val photoFragment = PhotoFragment()
            photoFragment.arguments = bundle

            photoFragments.add(photoFragment)
        }

        val photoPagerAdapter = PhotoPagerAdapter(supportFragmentManager)
        photoPagerAdapter.photoFragments.addAll(photoFragments)

        photo_view_pager.adapter = photoPagerAdapter
        photo_view_pager.offscreenPageLimit = photoPagerAdapter.count
        photo_view_pager.currentItem = intent.getIntExtra(KeyName.POSITION, 0)
    }
}
