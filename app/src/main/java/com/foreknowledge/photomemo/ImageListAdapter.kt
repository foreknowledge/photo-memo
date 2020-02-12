package com.foreknowledge.photomemo

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image.view.*

class ImageListAdapter(private val context: Context, private val images: List<Bitmap>) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    class ImageViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.view.image_preview.setImageBitmap(images[position])
        holder.view.image_delete.setOnClickListener {
            Toast.makeText(context, "position = $position 아이템 삭제", Toast.LENGTH_SHORT).show()
        }
    }
}