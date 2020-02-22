package com.foreknowledge.photomemo

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_memo.view.*

interface ItemClickListener {
    fun onClick(view: View, position: Int)
}

class MemoListAdapter(private val memoList: List<Memo>, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<MemoListAdapter.MemoViewHolder>() {

    fun getItem(position: Int) = memoList[position]

    class MemoViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun getItemCount(): Int = memoList.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]

        if (memo.title.isBlank()) {
            holder.view.memo_title.visibility = View.GONE
            holder.view.memo_content.maxLines = 4
        }
        else
            holder.view.memo_title.text = memo.title

        if (memo.content.isBlank())
            holder.view.memo_content.visibility = View.GONE
        else
            holder.view.memo_content.text = memo.content

        if (memo.imagePaths.isEmpty())
            holder.view.thumbnail_card_view.visibility = View.GONE
        else {
            holder.view.thumbnail_image.setImageBitmap(BitmapFactory.decodeFile(memo.imagePaths[0]))
            holder.view.num_of_images.text = memo.imagePaths.size.toString()
        }


        holder.view.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }
}