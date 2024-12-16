package com.huiliang.aidemo.fragment

import com.huiliang.aidemo.R
import com.huiliang.aidemo.databinding.FragmentMapStorageBinding
import com.huiliang.aidemo.vm.AiViewModel
import com.huiliang.lib_base.ui.BaseFragment

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
    override fun getContentLayoutId(): Int {
        return R.layout.fragment_map_storage
    }

    override fun bindViewModel() {

    }
}