package com.foreknowledge.photomemo

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class PreviewImageManager(private val context: Context, imagePaths: List<String>?): PreviewImageListAdapter.OnStartDragListener {

    private lateinit var itemTouchHelper: ItemTouchHelper
    private val previewAdapter =
        if (imagePaths != null)
            PreviewImageListAdapter(context, imagePaths.toMutableList(), this)
        else
            PreviewImageListAdapter(context, mutableListOf(), this)

    fun setAdapterTo(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter = previewAdapter
    }

    fun revertImages() = previewAdapter.revertImages()
    fun getImages() = previewAdapter.getImages()
    fun isImageEmpty() = previewAdapter.getImages().isEmpty()
    fun isImageFull() = previewAdapter.isImageFull()
    fun addImageToAdapter(imagePath: String) = previewAdapter.addImagePath(BitmapHelper.rotateAndCompressImage(imagePath))

    fun importImageFrom(url: String) {
        ImageLoadTask(context, url, previewAdapter).execute()
    }

    fun setPreviewItemTouchCallbackAt(recyclerView: RecyclerView) {
        val callback = PreviewItemTouchCallback(previewAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onStartDrag(viewHolder: ImageListAdapter.ImageViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}
