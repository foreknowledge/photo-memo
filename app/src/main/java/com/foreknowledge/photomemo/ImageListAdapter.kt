package com.foreknowledge.photomemo

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_image_preview.view.*

abstract class ImageListAdapter(private val context: Context, private val imagePaths: List<String>, private val resource: Int) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    class ImageViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = imagePaths.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(resource, parent, false)

        return ImageViewHolder(view)
    }
}

class DetailImageListAdapter(private val context: Context, private val imagePaths: List<String>, private val memoId: Long) : ImageListAdapter(context, imagePaths, R.layout.item_image) {
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.view.iv_image_item.setImageBitmap(BitmapFactory.decodeFile(imagePaths[position]))
        holder.view.iv_image_item.setOnClickListener {
            val extras = Bundle()
            extras.putLong(KeyName.MEMO_ID, memoId)
            extras.putInt(KeyName.POSITION, position)

            switchTo(context, PhotoViewActivity::class.java, extras)
        }
    }
}

class PreviewImageListAdapter(private val context: Context, private val imagePaths: MutableList<String>, private val onStartDragListener: OnStartDragListener)
    : ImageListAdapter(context, imagePaths, R.layout.item_image_preview), PreviewItemTouchCallback.OnItemMoveListener {
    companion object {
        const val MAX_IMAGE_COUNT = 10

        const val ADD_IMAGE = 1
        const val DELETE_IMAGE = -1
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: ImageViewHolder)
    }

    data class ImageHistory(val type: Int, val imagePath: String)

    private val originalImgPaths = imagePaths.toMutableList()
    private val history = mutableListOf<ImageHistory>()

    fun getImages() : List<String> {
        for (action in history)
            when (action.type) {
                DELETE_IMAGE -> FileHelper.deleteFile(action.imagePath)
            }

        return imagePaths
    }

    fun revertImages(): List<String> {
        for (action in history)
            when (action.type) {
                ADD_IMAGE -> FileHelper.deleteFile(action.imagePath)
            }

        return originalImgPaths
    }

    fun addImagePath(imagePath: String) {
        history.add(ImageHistory(ADD_IMAGE, imagePath))
        imagePaths.add(imagePath)
        this.notifyDataSetChanged()

        if (context is CreateMemoActivity) {
            context.quitLoading()
            context.focusToBottom()
        }
    }

    fun showErrorMessage(msg: String) {
        if (context is CreateMemoActivity)
            context.quitLoading()

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun isImageFull() = imagePaths.size == MAX_IMAGE_COUNT

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.view.image_preview.setImageBitmap(BitmapFactory.decodeFile(imagePaths[position]))
        holder.view.image_delete.setOnClickListener {
            history.add(ImageHistory(DELETE_IMAGE, imagePaths[position]))
            imagePaths.removeAt(position)
            notifyDataSetChanged()
        }

        holder.view.image_preview.setOnTouchListener { _, motionEvent ->
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN)
                onStartDragListener.onStartDrag(holder)

            false
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val target = imagePaths[fromPosition]
        imagePaths.removeAt(fromPosition)
        imagePaths.add(toPosition, target)

        notifyItemMoved(fromPosition, toPosition)
    }
}

class PhotoRecyclerAdapter(private val context: Context, private val imagePaths: List<String>) : RecyclerView.Adapter<PhotoRecyclerAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(val view: PhotoView): RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = imagePaths.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val photoView = PhotoView(context)
        photoView.layoutParams = (ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        return PhotoViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.view.setImageBitmap(BitmapFactory.decodeFile(imagePaths[position]))
    }
}