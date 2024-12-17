package com.xinlicheng.lib_base.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

/**
 * Time: 4/13/21
 * Author: muse
 * QQ: 554953278
 * Description:
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(val mContext: Context) :
    RecyclerView.Adapter<VH>() {
    //ItemClick事件
    var mItemClickListener: OnItemClickListener<T>? = null

    var mItemLongClickListener: OnItemLongClickListener<T>? = null

    //数据集合
    var dataList: MutableList<T> = mutableListOf()

    /*
        设置数据
        Presenter处理过为null的情况，所以为不会为Null
     */
    fun setData(sources: MutableList<T>) {
        dataList.clear()
        dataList = sources
        notifyDataSetChanged()
    }

    fun addAll(sources: MutableList<T>) {
        dataList.addAll(sources)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        dataList.removeAt(position)
        notifyDataSetChanged()
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun notifyDataSetChang() {
        notifyDataSetChanged()
    }

    fun replaceAll(sources: MutableList<T>){
        dataList.apply {
            clear()
            addAll(sources)
        }
        notifyDataSetChang()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(dataList[position], position)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    /*
      ItemClick事件声明
   */
    interface OnItemClickListener<in T> {
        fun onItemClick(item: T, position: Int)
    }

    interface OnItemLongClickListener<in T> {
        fun onItemLongClick(item: T, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<T>) {
        this.mItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener<T>) {
        this.mItemLongClickListener = listener
    }

}