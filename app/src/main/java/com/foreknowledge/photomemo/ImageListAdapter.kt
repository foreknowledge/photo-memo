package com.foreknowledge.photomemo

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_image_preview.view.*

class DetailImageListAdapter(context: Context, private val images: List<Bitmap>) : ImageListAdapter(context, images, R.layout.item_image) {
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.view.iv_image_item.setImageBitmap(images[position])
    }
}

class PreviewImageListAdapter(private val context: Context, private val images: MutableList<Bitmap>) : ImageListAdapter(context, images, R.layout.item_image_preview) {
    companion object {
        const val MAX_IMAGE_COUNT = 10
    }

    fun getAllItems() = images

    fun addImages(images: List<Bitmap>) {
        for (bitmap in images) addImage(bitmap)
    }

    fun addImage(image: Bitmap?) {
        image?.let {
            if (images.size < MAX_IMAGE_COUNT) {
                images.add(image)
                this.notifyDataSetChanged()
            } else Toast.makeText(context, "이미지 첨부는 ${MAX_IMAGE_COUNT}개까지만 가능합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.view.image_preview.setImageBitmap(images[position])
        holder.view.image_delete.setOnClickListener {
            images.removeAt(position)
            notifyDataSetChanged()
        }
    }
}

abstract class ImageListAdapter(private val context: Context, private val images: List<Bitmap>, private val resource: Int) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    class ImageViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(resource, parent, false)
        return ImageViewHolder(view)
    }
}