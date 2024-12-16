package com.huiliang.aidemo.fragment

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.utils.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.huiliang.aidemo.R
import com.huiliang.aidemo.databinding.FragmentVincentDiagramBinding
import com.huiliang.aidemo.vm.AiViewModel
import com.huiliang.lib_base.ui.BaseFragment
import com.huiliang.lib_net.ResultByCoroutine
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView


/**
 * Time: 2024/11/19
 * Author: muse
 * QQ: 554953278
 * Description:文生图
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
class VincentDiagramFragment : BaseFragment<AiViewModel, FragmentVincentDiagramBinding>() {
    private var popupView: BasePopupView? = null
    private var currentModel: String = "dall-e-2"
    private var currentResolution: String = "256x256"
    private var isFirst = true
    private var edText: String = ""

    override fun getContentLayoutId(): Int = R.layout.fragment_vincent_diagram

    override fun bindViewModel() {
        // 初始化加载弹窗
        popupView = XPopup.Builder(context).asLoading("Loading")

        // 观察 ViewModel 的数据变化
        mViewModel.apply {
            loading.observe(viewLifecycleOwner, Observer { isLoading ->
                toggleLoading(isLoading)
            })

            aiPictureInfoData.observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is ResultByCoroutine.Success -> {
                        LogUtils.e(result.data[0].image_url)
                        loadImage(result.data[0].image_url)
                    }

                    else -> handleResult(result)
                }
            })

            initView()
        }
    }

    // 显示/隐藏加载弹窗
    private fun toggleLoading(isLoading: Boolean) {
        if (isLoading) {
            popupView?.takeIf { !it.isShow }?.show()
        } else {
            popupView?.takeIf { it.isShow }?.dismiss()
        }
    }

    // 加载图片
    private fun loadImage(url: String) {
        Glide.with(this@VincentDiagramFragment)
            .load(url)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.error)
            .into(mViewBinding.ivImg)
    }

    // 初始化视图组件
    private fun initView() {
        mViewBinding.apply {
            llModel.setOnClickListener { showModelSelectionPopup() }
            llResolution.setOnClickListener { showResolutionSelectionPopup() }
            btSearch.setOnClickListener { onSearchClicked() }
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    s?.apply {
                        isFirst = edText != s.toString()
                    }
                }

            })
        }
    }

    // 显示模型选择弹窗
    private fun showModelSelectionPopup() {
        XPopup.Builder(context)
            .atView(mViewBinding.llModel)
            .asAttachList(
                arrayOf("dall-e-2", "dall-e-3"),
                intArrayOf()
            ) { position, text ->
                mViewBinding.tvModel.text = text
                currentModel = text
                when (position) {
                    0 -> {
                        mViewBinding.tvResolution.text = "256x256"
                        currentResolution = "256x256"
                    }

                    1 -> {
                        mViewBinding.tvResolution.text = "1024x1024"
                        currentResolution = "1024x1024"
                    }
                }
            }
            .show()
    }

    // 显示分辨率选择弹窗
    private fun showResolutionSelectionPopup() {
        val resolutions = when (currentModel) {
            "dall-e-2" -> arrayOf("1024x1024", "512x512", "256x256")
            "dall-e-3" -> arrayOf("1792x1024", "1024×1792", "1024x1024")
            else -> arrayOf("256x256")
        }

        XPopup.Builder(context)
            .atView(mViewBinding.llResolution)
            .asAttachList(resolutions, intArrayOf()) { _, text ->
                mViewBinding.tvResolution.text = text
                currentResolution = text
            }
            .show()
    }

    // 搜索按钮点击事件
    private fun onSearchClicked() {
        val query = mViewBinding.etSearch.text.toString()
        LogUtils.e(query)

        if (TextUtils.isEmpty(query)) {
            ToastUtils.showShort("请先输入关键词")
        } else {
            edText = mViewBinding.etSearch.text.toString()
            mViewModel.apply {
                if (isFirst) {
                    getAIPicture(currentModel, currentResolution, edText)
                }
            }
        }
    }
}