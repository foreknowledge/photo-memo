package com.foreknowledge.photomemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.memo_item.view.*

class MemoListAdapter(private val memoList: List<Memo>) : RecyclerView.Adapter<MemoListAdapter.MemoViewHolder>() {

    class MemoViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.memo_item, parent, false)
        return MemoViewHolder(view)
    }

    override fun getItemCount(): Int = memoList.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]

        holder.view.memo_title.text = memo.title
        holder.view.memo_content.text = memo.content

        if (memo.thumbnail != null && memo.thumbnail.isNotEmpty())
            holder.view.thumbnail_image.setImageBitmap(memo.thumbnail[0])
        else
            holder.view.thumbnail_card_view.visibility = View.GONE
    }
}