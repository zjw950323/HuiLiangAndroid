package com.huiliang.aidemo.fragment

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.utils.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huiliang.aidemo.R
import com.huiliang.aidemo.bean.AIPicResponseBean
import com.huiliang.aidemo.databinding.FragmentVincentDiagramBinding
import com.huiliang.aidemo.vm.AiViewModel
import com.huiliang.lib_base.ui.BaseFragment
import com.huiliang.lib_base.utils.ImageSaver
import com.huiliang.lib_net.ResultByCoroutine
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.tencent.mmkv.MMKV
import java.lang.reflect.Type
import kotlin.random.Random


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
    private val mAiPicList: MutableList<AIPicResponseBean> = mutableListOf()
    private val type: Type = object : TypeToken<MutableList<AIPicResponseBean>>() {}.type
    private var url: String = ""

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
                        mAiPicList.apply {
                            clear()
                            addAll(result.data)
                        }
                        if (mAiPicList.isNotEmpty()) {
                            loadImage()
                        }
                    }

                    else -> handleResult(result)
                }
            })
            aiPictureGenerateData.observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is ResultByCoroutine.Success -> {
                        mAiPicList.apply {
                            clear()
                            addAll(result.data)
                        }
                        if (mAiPicList.isNotEmpty()) {
                            loadImage()
                        }
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
    private fun loadImage() {
        // 获取一个随机索引
        val randomIndex: Int = Random.nextInt(mAiPicList.size)
        LogUtils.e(randomIndex)
//        val oldJson = MMKV.defaultMMKV().decodeString("aiPicList", "")
//        if (!oldJson.isNullOrEmpty()) {
//            val oldPicList: MutableList<AIPicResponseBean> = Gson().fromJson(oldJson, type)
//            oldPicList.add(mAiPicList[randomIndex])
//            val json = Gson().toJson(oldPicList)
//            MMKV.defaultMMKV().encode("aiPicList", json)
//        } else {
//            val json = Gson().toJson(mAiPicList[randomIndex])
//            MMKV.defaultMMKV().encode("aiPicList", json)
//        }
        // 保存新图片到 MMKV
        saveImageToMMKV(mAiPicList[randomIndex])
        url = mAiPicList[randomIndex].image_url
        Glide.with(this@VincentDiagramFragment).load(url).placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.error).into(mViewBinding.ivImg)
        mAiPicList.removeAt(randomIndex)
    }

    // 初始化视图组件
    private fun initView() {
        mViewBinding.apply {
            llModel.setOnClickListener { showModelSelectionPopup() }
            llResolution.setOnClickListener { showResolutionSelectionPopup() }
            btSearch.setOnClickListener { onSearchClicked() }
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
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
            btSave.setOnClickListener {
                if (url.isNotEmpty()) {
                    savePic()
                } else {
                    ToastUtils.showShort("请先想象图片")
                }
            }
        }
    }

    // 显示模型选择弹窗
    private fun showModelSelectionPopup() {
        XPopup.Builder(context).atView(mViewBinding.llModel).asAttachList(
            arrayOf("dall-e-2", "dall-e-3"), intArrayOf()
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
        }.show()
    }

    // 显示分辨率选择弹窗
    private fun showResolutionSelectionPopup() {
        val resolutions = when (currentModel) {
            "dall-e-2" -> arrayOf("1024x1024", "512x512", "256x256")
            "dall-e-3" -> arrayOf("1792x1024", "1024×1792", "1024x1024")
            else -> arrayOf("256x256")
        }

        XPopup.Builder(context).atView(mViewBinding.llResolution)
            .asAttachList(resolutions, intArrayOf()) { _, text ->
                mViewBinding.tvResolution.text = text
                currentResolution = text
            }.show()
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
                //如果是第一次请求
                if (isFirst) {
                    isFirst = false
                    //调用后台接口查询数据库是否有相似的图片
                    getAIPicture(currentModel, currentResolution, edText)
                } else {
                    //如果从数据库中获取的图片用完了
                    if (mAiPicList.isEmpty()) {
                        //直接生成一张新图片
                        getImageGenerate(currentModel, currentResolution, edText)
                    } else {
                        //继续使用从数据库中获取的图片
                        loadImage()
                    }
                }
            }
        }
    }

    //保存当前图片
    private fun savePic() {
        val fileName = "network_image_${System.currentTimeMillis()}.jpg" // 自定义文件名
        // 保存图片到相册
        context?.let { ImageSaver.saveNetworkImageToGallery(it, url, fileName) }
    }

    //缓存图片数据到MMKV
    private fun saveImageToMMKV(newImage: AIPicResponseBean) {
        val mmkv = MMKV.defaultMMKV()
        val oldJson = mmkv.decodeString("aiPicList", "")

        val picList: MutableList<AIPicResponseBean> = if (!oldJson.isNullOrEmpty()) {
            // 如果有旧数据，将其转换为 List
            Gson().fromJson(oldJson, type)
        } else {
            // 如果没有旧数据，初始化空列表
            mutableListOf()
        }

        // 添加新数据到列表
        picList.add(newImage)

        // 将更新后的列表存回 MMKV
        val newJson = Gson().toJson(picList)
        mmkv.encode("aiPicList", newJson)
    }
}