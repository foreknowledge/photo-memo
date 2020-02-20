package com.foreknowledge.photomemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_photo_view.*

class PhotoViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        setPhotoViewPager()

        btn_go_before.setOnClickListener { finish() }
    }

    private fun setPhotoViewPager() {
        val imagePaths = MemoDbTable(this).readImagePaths(intent.getLongExtra(KeyName.MEMO_ID, 0))

        val currentPosition = intent.getIntExtra(KeyName.POSITION, 0)
        val totalImageCount = imagePaths.size

        photo_view_pager.adapter = PhotoRecyclerAdapter(this, imagePaths)
        photo_view_pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        photo_view_pager.setCurrentItem(currentPosition, false)
        setPageNumber(currentPosition, totalImageCount)

        photo_view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setPageNumber(position, totalImageCount)
            }
        })
    }

    private fun setPageNumber(position: Int, totalCount: Int) {
        val text = "${position + 1} / $totalCount"
        page_indicator.text = text
    }
}
