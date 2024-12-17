package com.huiliang.aidemo.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huiliang.aidemo.R
import com.huiliang.aidemo.adapter.MapStorageAdapter
import com.huiliang.aidemo.bean.AIPicResponseBean
import com.huiliang.aidemo.databinding.FragmentMapStorageBinding
import com.huiliang.aidemo.vm.AiViewModel
import com.huiliang.lib_base.ui.BaseFragment
import com.huiliang.lib_base.utils.GridDecoration
import com.tencent.mmkv.MMKV
import java.lang.reflect.Type

/**
 * Time: 2024/12/13
 * Author: muse
 * QQ: 554953278
 * Description:图库
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
class MapStorageFragment : BaseFragment<AiViewModel, FragmentMapStorageBinding>() {
    private val type: Type = object : TypeToken<MutableList<AIPicResponseBean>>() {}.type
    private val picList: MutableList<AIPicResponseBean> = mutableListOf()
    override fun getContentLayoutId(): Int {
        return R.layout.fragment_map_storage
    }

    override fun bindViewModel() {
        initView()
    }

    private fun initView() {
        mViewBinding.apply {
            getPicList()
            if (picList.isNotEmpty()) {
                rvList.apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = MapStorageAdapter(context).apply {
                        setData(picList)
                    }
                    addItemDecoration(
                        GridDecoration(
                            ConvertUtils.dp2px(10f),
                            ConvertUtils.dp2px(25f),
                            true
                        )
                    )
                }
            }

        }
    }

    private fun getPicList() {
        val mmkv = MMKV.defaultMMKV()
        val oldJson = mmkv.decodeString("aiPicList", "")

        if (!oldJson.isNullOrEmpty()) {
            // 如果有旧数据，将其转换为 List
            picList.addAll(Gson().fromJson(oldJson, type))
        } else {
            // 如果没有旧数据，初始化空列表
            picList.clear()
        }
    }

}