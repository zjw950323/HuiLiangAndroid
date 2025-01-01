package com.huiliang.picturesque.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huiliang.lib_base.ui.BaseFragment
import com.huiliang.lib_base.utils.GridDecoration
import com.huiliang.picturesque.R
import com.huiliang.picturesque.adapter.MapStorageAdapter
import com.huiliang.picturesque.bean.AIPicResponseBean
import com.huiliang.picturesque.databinding.FragmentMapStorageBinding
import com.huiliang.picturesque.vm.AiViewModel
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