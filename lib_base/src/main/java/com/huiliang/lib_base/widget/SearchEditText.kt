package com.huiliang.lib_base.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.huiliang.lib_base.R

/**
 * Time: 2024/11/19
 * Author: muse
 * QQ: 554953278
 * Description:用于搜索的 EditText
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */

class SearchEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val container: LinearLayout
    private val editText: EditText
    private var lastClickTime = 0L

    private var onSearchChangeListener: OnSearchChangeListener? = null

    interface OnSearchChangeListener {
        fun onSearchChange(query: String)
        fun onRemoveView(position: Int)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.search_edittext_layout, this, true)
        container = findViewById(R.id.layout)
        editText = findViewById(R.id.edittext)

        editText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL && isNotFastClick()) {
                handleDeleteKey()
                true
            } else {
                false
            }
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearchAction()
                true
            } else {
                false
            }
        }
    }

    private fun handleDeleteKey() {
        val text = editText.text.toString()
        if (text.isNotEmpty()) {
            editText.setText(text.dropLast(1))
            editText.setSelection(editText.length())
        } else if (container.childCount > 0) {
            container.removeViewAt(container.childCount - 1)
            onSearchChangeListener?.onRemoveView(container.childCount)
        }
    }

    private fun handleSearchAction() {
        val query = editText.text.toString().trim()
        if (query.isNotEmpty()) {
            addSearchTag(query)
            onSearchChangeListener?.onSearchChange(query)
            editText.text.clear()
        }
    }

    private fun addSearchTag(query: String) {
        val textView = TextView(context).apply {
            text = query
            textSize = 14f
            setTextColor(Color.parseColor("#dfe0e0"))
            setPadding(10, 0, 10, 0)
            setBackgroundResource(R.drawable.shape_edittext_round_bg)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { leftMargin = 10 }
        }
        container.addView(textView)
    }

    private fun isNotFastClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastClickTime >= 300) {
            lastClickTime = currentTime
            true
        } else {
            false
        }
    }

    fun getEditText(): EditText = editText

    fun getContainer(): LinearLayout = container
}
