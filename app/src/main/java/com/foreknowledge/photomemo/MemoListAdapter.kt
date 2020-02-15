package com.foreknowledge.photomemo

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

    open class MemoViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun getItemCount(): Int = memoList.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]

        holder.view.memo_title.text = memo.title
        holder.view.memo_content.text = memo.content

        if (memo.images.isNotEmpty()) {
            holder.view.thumbnail_image.setImageBitmap(memo.images[0])
            holder.view.num_of_images.text = memo.images.size.toString()
        }
        else
            holder.view.thumbnail_card_view.visibility = View.GONE

        holder.view.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }
}