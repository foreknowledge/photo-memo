package com.foreknowledge.photomemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_photo_view.*

class PhotoViewActivity : AppCompatActivity() {

    lateinit var photoPagerAdapter: PhotoPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        setPhotoPagerAdapter()
        setPhotoViewPager()

        btn_go_before.setOnClickListener { finish() }
    }

    private fun setPhotoPagerAdapter() {
        val photoFragments = mutableListOf<PhotoFragment>()

        val imagePaths = MemoDbTable(this).readImagePaths(intent.getLongExtra(KeyName.MEMO_ID, 0))

        for (path in imagePaths) {
            val bundle = Bundle()
            bundle.putString(KeyName.IMAGE_PATH, path)

            val photoFragment = PhotoFragment()
            photoFragment.arguments = bundle

            photoFragments.add(photoFragment)
        }

        photoPagerAdapter = PhotoPagerAdapter(supportFragmentManager)
        photoPagerAdapter.photoFragments.addAll(photoFragments)
    }

    private fun setPhotoViewPager() {
        photo_view_pager.adapter = photoPagerAdapter
        photo_view_pager.offscreenPageLimit = photoPagerAdapter.count
        photo_view_pager.currentItem = intent.getIntExtra(KeyName.POSITION, 0)

        photo_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val text = "${position + 1} / ${photoPagerAdapter.count}"
                page_indicator.text = text
            }

            override fun onPageSelected(position: Int) {}
        })
    }
}
