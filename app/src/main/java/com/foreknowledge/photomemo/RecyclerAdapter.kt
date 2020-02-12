package com.foreknowledge.photomemo

import android.view.View

//class MemoListAdapter(private val memoList: List<Memo>, itemClickListener: ItemClickListener) : BaseRecyclerAdapter(memoList, itemClickListener) {
//
//    override val resource: Int = R.layout.item_memo
//
//    override fun bindViewHolderFunc(holder: ViewHolder, position: Int) {
//        val memo = memoList[position]
//
//        holder.view.memo_title.text = memo.title
//        holder.view.memo_content.text = memo.content
//
//        if (memo.thumbnail != null && memo.thumbnail.isNotEmpty())
//            holder.view.thumbnail_image.setImageBitmap(memo.thumbnail[0])
//        else
//            holder.view.thumbnail_image.visibility = View.GONE
//    }
//}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


interface ItemClickListener {
    fun onClick(view: View, position: Int)
}

//abstract class BaseRecyclerAdapter(private val itemList: List<Any>, private val itemClickListener: ItemClickListener? = null) : RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder>() {
//
//    abstract val resource: Int
//    abstract fun bindViewHolderFunc(holder: ViewHolder, position: Int)
//
//    fun getItem(position: Int) = itemList[position]
//
//    open class ViewHolder(val view: View): RecyclerView.ViewHolder(view)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view: View = LayoutInflater.from(parent.context).inflate(resource, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = itemList.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        bindViewHolderFunc(holder, position)
//
//        itemClickListener?.let {
//            holder.view.setOnClickListener{
//                itemClickListener.onClick(it, position)
//            }
//        }
//    }
//}