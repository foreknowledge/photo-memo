package com.foreknowledge.photomemo

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image.view.*

class ImageListAdapter(private val context: Context, private val images: MutableList<Bitmap>) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {
    companion object {
        const val MAX_IMAGE_COUNT = 10
    }

    fun addImage(bitmap: Bitmap): Boolean {
        if (images.size < MAX_IMAGE_COUNT) {
            images.add(bitmap)
            return true
        }
        return false
    }

    class ImageViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.view.image_preview.setImageBitmap(images[position])
        holder.view.image_delete.setOnClickListener {
            images.removeAt(position)
            notifyDataSetChanged()
        }
    }
}