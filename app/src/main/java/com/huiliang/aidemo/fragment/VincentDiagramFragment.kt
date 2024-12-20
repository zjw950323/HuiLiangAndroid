package com.huiliang.aidemo.fragment

import android.Manifest
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import com.alibaba.android.arouter.utils.TextUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huiliang.aidemo.BuildConfig
import com.huiliang.aidemo.R
import com.huiliang.aidemo.bean.AIPicResponseBean
import com.huiliang.aidemo.bean.UpdateAppBean
import com.huiliang.aidemo.databinding.FragmentVincentDiagramBinding
import com.huiliang.aidemo.util.ApkInstaller
import com.huiliang.aidemo.vm.AiViewModel
import com.huiliang.lib_base.ui.BaseFragment
import com.huiliang.lib_base.utils.ImageSaver
import com.huiliang.lib_net.ResultByCoroutine
import com.huiliang.lib_net.SimpleConstant
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.LoadingPopupView
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.tencent.mmkv.MMKV
import java.lang.reflect.Type
import kotlin.math.max
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
    private var isFirst = true
    private var edText: String = ""
    private val mAiPicList: MutableList<AIPicResponseBean> = mutableListOf()
    private val type: Type = object : TypeToken<MutableList<AIPicResponseBean>>() {}.type
    private var url: String = ""
    private val versionName = BuildConfig.VERSION_NAME
    private var progressDialog: LoadingPopupView? = null
    private var isClick = false
    private var needUpdate = true
    private val permission: MutableList<String> = mutableListOf()

    // 新增标志位，用于跟踪图片加载状态
    private var isImageLoading = false

    override fun getContentLayoutId(): Int = R.layout.fragment_vincent_diagram

    override fun bindViewModel() {
        // 初始化加载弹窗
        popupView = XPopup.Builder(context).asLoading("Loading")

        // 观察 ViewModel 的数据变化
        mViewModel.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permission.add(Manifest.permission.READ_MEDIA_IMAGES)
                permission.add(Manifest.permission.READ_MEDIA_AUDIO)
                permission.add(Manifest.permission.READ_MEDIA_VIDEO)
                permission.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            } else {
                permission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            PermissionX.init(this@VincentDiagramFragment)
                .permissions(permission)
                .onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        "Core fundamental are based on these permissions",
                        "OK",
                        "Cancel"
                    )
                }
                .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                    LogUtils.e(allGranted)
                    LogUtils.e(grantedList)
                    LogUtils.e(deniedList)
                    if (allGranted) {
                        if (needUpdate) {
                            getUpdateVersion()
                        }
                    }
                }

            loading.observe(this@VincentDiagramFragment) { isLoading ->
                if (isLoading) {
                    toggleLoading(true)
                } else {
                    if (!isImageLoading) {
                        toggleLoading(false)
                    }
                }
            }

            versionNumber.observe(this@VincentDiagramFragment) { result ->
                when (result) {
                    is ResultByCoroutine.Success -> {
                        if (needUpdate) {
                            needUpdate = false
                            val newVersion = result.data.version_number
                            if (isNewerVersion(newVersion, versionName)) {
                                showUpdateDialog(result.data)
                            }
                        }
                    }

                    else -> handleResult(result)
                }
            }

            aiPictureInfoData.observe(this@VincentDiagramFragment) { result ->
                when (result) {
                    is ResultByCoroutine.Success -> {
                        if (isClick) {
                            isImageLoading = true
                            mAiPicList.apply {
                                clear()
                                addAll(result.data)
                            }
                            if (mAiPicList.isNotEmpty()) {
                                loadImage()
                            }
                        }
                    }

                    else -> handleResult(result)
                }
            }
            aiPictureGenerateData.observe(this@VincentDiagramFragment) { result ->
                when (result) {
                    is ResultByCoroutine.Success -> {
                        if (isClick) {
                            isImageLoading = true
                            mAiPicList.apply {
                                clear()
                                addAll(result.data)
                            }
                            if (mAiPicList.isNotEmpty()) {
                                loadImage()
                            }
                        }
                        isClick = false
                    }

                    else -> handleResult(result)
                }
            }

            //下载进度
            downloadProgress.observe(this@VincentDiagramFragment) { result ->
                when (result) {
                    is ResultByCoroutine.Success -> {
                        updateDownloadProgress(result.data)
                    }

                    else -> handleResult(result)
                }
            }

            //下载完成
            downloadComplete.observe(this@VincentDiagramFragment) { result ->
                when (result) {
                    is ResultByCoroutine.Success -> {
                        handleDownloadComplete(result.data)
                    }

                    else -> handleResult(result)
                }
            }
            initView()
        }
    }

    // 显示/隐藏加载弹窗
    private fun toggleLoading(isLoading: Boolean) {
        if (isLoading) {
            // 如果弹窗没有显示，则显示弹窗
            if (popupView?.isShow == false) {
                popupView?.show()
            }
        } else {
            // 如果弹窗正在显示，则隐藏弹窗
            if (popupView?.isShow == true) {
                popupView?.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 释放 popupView 引用
        popupView = null
        progressDialog = null
    }

    override fun onStop() {
        super.onStop()
        // 当 Fragment 停止时，确保隐藏 loading 弹窗
        toggleLoading(false)
    }

    // 加载图片
    private fun loadImage() {
        isClick = false
        LogUtils.e(mAiPicList.size)
        // 获取一个随机索引
        val randomIndex: Int = Random.nextInt(mAiPicList.size)
        // 保存新图片到 MMKV
        saveImageToMMKV(mAiPicList[randomIndex])
        url = SimpleConstant().BASE_PIC_URL + mAiPicList[randomIndex].image_name
        LogUtils.e(url)
        Glide.with(this@VincentDiagramFragment).load(url).placeholder(R.drawable.placeholder)
            .error(R.drawable.error).listener(object :RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // 图片加载失败，隐藏 loading 弹窗
                    isImageLoading = false
                    toggleLoading(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // 图片加载成功，隐藏 loading 弹窗
                    isImageLoading = false
                    toggleLoading(false)
                    return false
                }

            }).into(mViewBinding.ivImg)
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
            isFirst = true
            when (position) {
                0 -> {
                    mViewBinding.tvResolution.text = "256x256"
                }

                1 -> {
                    mViewBinding.tvResolution.text = "1024x1024"
                }
            }
        }.show()
    }

    // 显示分辨率选择弹窗
    private fun showResolutionSelectionPopup() {
        if (TextUtils.isEmpty(mViewBinding.tvModel.text)) {
            ToastUtils.showShort("请先选择模型")
            return
        }
        val resolutions = when (mViewBinding.tvModel.text) {
            "dall-e-2" -> arrayOf("1024x1024", "512x512", "256x256")
            "dall-e-3" -> arrayOf("1792x1024", "1024×1792", "1024x1024")
            else -> arrayOf("256x256")
        }

        XPopup.Builder(context).atView(mViewBinding.llResolution)
            .asAttachList(resolutions, intArrayOf()) { _, text ->
                mViewBinding.tvResolution.text = text
                isFirst = true
            }.show()
    }

    // 搜索按钮点击事件
    private fun onSearchClicked() {
        val model = mViewBinding.tvModel.text.toString()
        val size = mViewBinding.tvResolution.text.toString()
        edText = mViewBinding.etSearch.text.toString()

        if (TextUtils.isEmpty(model)) {
            ToastUtils.showShort("请先选择模型")
            return
        }
        if (TextUtils.isEmpty(size)) {
            ToastUtils.showShort("请先选择分辨率")
            return
        }
        if (TextUtils.isEmpty(edText)) {
            ToastUtils.showShort("请先输入关键词")
            return
        }


        isClick = true
        mViewModel.apply {
            //如果是第一次请求
            if (isFirst) {
                isFirst = false
                //调用后台接口查询数据库是否有相似的图片
                getAIPicture(model, size, edText)
            } else {
                //如果从数据库中获取的图片用完了
                if (mAiPicList.isEmpty()) {
                    //直接生成一张新图片
                    getImageGenerate(model, size, edText)
                } else {
                    //继续使用从数据库中获取的图片
                    loadImage()
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

    //判定是否有更新
    private fun isNewerVersion(latestVersion: String, currentVersion: String): Boolean {
        LogUtils.e("Comparing versions - Latest: $latestVersion, Current: $currentVersion")
        if (latestVersion.isEmpty() || currentVersion.isEmpty()) {
            LogUtils.e("One of the versions is empty, cannot compare")
            return false
        }

        val latest =
            latestVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val current =
            currentVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (i in 0 until max(latest.size.toDouble(), current.size.toDouble()).toInt()) {
            val l = if (i < latest.size) latest[i].toInt() else 0
            val c = if (i < current.size) current[i].toInt() else 0
            if (l > c) {
                LogUtils.e("New version is newer")
                return true
            } else if (l < c) {
                LogUtils.e("Current version is newer")
                return false
            }
        }

        LogUtils.e("Versions are equal")
        return false
    }

    private fun showUpdateDialog(updateAppBean: UpdateAppBean) {
        XPopup.Builder(context)
            .asConfirm("发现新版本 ${updateAppBean.version_number}",
                updateAppBean.update_description,
                {
                    mViewModel.downloadUpdate(updateAppBean.update_url)
                    showProgressDialog()
                },
                {
                    if (updateAppBean.is_force_update == 1) {
                        AppUtils.exitApp()
                    }
                })
            .show()
    }

    private fun showProgressDialog() {
        progressDialog = XPopup.Builder(context)
            .dismissOnBackPressed(false)
            .asLoading("正在下载更新...")
            .show() as LoadingPopupView
    }

    //下载进度
    private fun updateDownloadProgress(progress: Int) {
        LogUtils.e(progress)
        progressDialog?.setTitle("正在下载更新...$progress%")
    }

    //下载完成 安装
    private fun handleDownloadComplete(filePath: String) {
        progressDialog?.apply {
            if (isShow) {
                dismiss()
            }
        }
        ApkInstaller.installApk(context, filePath)
    }
}