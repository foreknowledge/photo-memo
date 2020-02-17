package com.foreknowledge.photomemo

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_image_preview.view.*

class DetailImageListAdapter(context: Context, private val imagePaths: List<String>) : ImageListAdapter(context, imagePaths, R.layout.item_image) {
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.view.iv_image_item.setImageBitmap(BitmapFactory.decodeFile(imagePaths[position]))
    }
}

class PreviewImageListAdapter(context: Context, private val imagePaths: MutableList<String>) : ImageListAdapter(context, imagePaths, R.layout.item_image_preview) {
    companion object {
        const val MAX_IMAGE_COUNT = 10

        const val ADD_IMAGE = 1
        const val DELETE_IMAGE = -1
    }

    data class ImageHistory(val type: Int, val imagePath: String)

    private val originalImgPaths = imagePaths.toMutableList()
    private val history = mutableListOf<ImageHistory>()

    fun reflect() : List<String> {
        for (action in history)
            when (action.type) {
                DELETE_IMAGE -> FileHelper.deleteFile(action.imagePath)
            }

        return imagePaths
    }

    fun undo(): List<String> {
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
    }

    fun isFull() = imagePaths.size == MAX_IMAGE_COUNT

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.view.image_preview.setImageBitmap(BitmapFactory.decodeFile(imagePaths[position]))
        holder.view.image_delete.setOnClickListener {
            history.add(ImageHistory(DELETE_IMAGE, imagePaths[position]))
            imagePaths.removeAt(position)
            notifyDataSetChanged()
        }
    }
}

abstract class ImageListAdapter(private val context: Context, private val imagePaths: List<String>, private val resource: Int) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    class ImageViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = imagePaths.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(resource, parent, false)
        return ImageViewHolder(view)
    }
}