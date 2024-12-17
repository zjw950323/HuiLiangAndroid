package com.huiliang.lib_base.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Time: 3/29/21
 * Author: muse
 * QQ: 554953278
 * Description:recyclerview的item添加间隔
 * ___       ___       ___       ___
 * /\__\     /\__\     /\  \     /\  \
 * /::L_L_   /:/ _/_   /::\  \   /::\  \
 * /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 * \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 * /:/  /   \::/  /   \::/  /   \:\/  /
 * \/__/     \/__/     \/__/     \/__/
 */
class GridDecoration : RecyclerView.ItemDecoration {
    protected var mSpace = 10
    protected var mAround = 15

    //是否上下也添加间隔
    private var mIncludeEdge = true

    constructor(space: Int) {
        mSpace = space
    }

    constructor(space: Int, around: Int=10, includeEdge: Boolean) {
        mSpace = space
        mAround = around
        mIncludeEdge = includeEdge
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val gridLayoutManager = parent.layoutManager as GridLayoutManager?
        //列数
        val spanCount = gridLayoutManager!!.spanCount
        val position = parent.getChildLayoutPosition(view)
        val column = position % spanCount
        if (mIncludeEdge) {
            outRect.left = mAround - column * mAround / spanCount
            outRect.right = (column + 1) * mAround / spanCount
            if (position < spanCount) {
                outRect.top = mSpace
            }
            outRect.bottom = mSpace
        } else {
            outRect.left = column * mAround / spanCount
            outRect.right = mAround - (column + 1) * mAround / spanCount
            if (position >= spanCount) {
                outRect.top = mSpace
            }
        }
    }
}
