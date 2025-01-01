package com.huiliang.picturesque.vm

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.google.gson.reflect.TypeToken
import com.huiliang.lib_net.NetworkRequestManager
import com.huiliang.lib_net.ResultByCoroutine
import com.huiliang.lib_net.vm.BaseViewModel
import com.huiliang.picturesque.api.AiApi
import com.huiliang.picturesque.bean.AIPicResponseBean
import com.huiliang.picturesque.bean.UpdateAppBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.WeakHashMap

class AiViewModel(context: Context) : BaseViewModel(context) {
    private val networkRequestManager = NetworkRequestManager.getInstance(context)
    private val apiService = networkRequestManager.createService(AiApi::class.java)

    //loading弹窗
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    //先查询后生成
    private val _aiPictureInfoData =
        MutableLiveData<ResultByCoroutine<List<AIPicResponseBean>>>()
    val aiPictureInfoData: LiveData<ResultByCoroutine<List<AIPicResponseBean>>> =
        _aiPictureInfoData

    fun getAIPicture(model: String, size: String, description: String) {
        val map = WeakHashMap<String, Any>()
        map["model"] = model
        map["size"] = size
        map["description"] = description
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val mAiPictureInfo = networkRequestManager.fetchListResult({
                    apiService.getImageList(map)
                }, object : TypeToken<List<AIPicResponseBean>>() {}, "getAIPicture")
                _aiPictureInfoData.value = mAiPictureInfo
            } finally {
                _loading.postValue(false)  // 确保在请求完成后隐藏 loading
            }
        }
    }

    //直接生成
    private val _aiPictureGenerateData =
        MutableLiveData<ResultByCoroutine<List<AIPicResponseBean>>>()
    val aiPictureGenerateData: LiveData<ResultByCoroutine<List<AIPicResponseBean>>> =
        _aiPictureGenerateData

    fun getImageGenerate(model: String, size: String, description: String) {
        val map = WeakHashMap<String, Any>()
        map["model"] = model
        map["size"] = size
        map["description"] = description
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val mAiPictureGenerateInfo = networkRequestManager.fetchListResult({
                    apiService.getImageGenerate(map)
                }, object : TypeToken<List<AIPicResponseBean>>() {}, "getImageGenerate")
                _aiPictureGenerateData.value = mAiPictureGenerateInfo
            } finally {
                _loading.postValue(false)  // 确保在请求完成后隐藏 loading
            }
        }
    }

    //获取版本号
    private val _versionNumber = MutableLiveData<ResultByCoroutine<UpdateAppBean>>()
    val versionNumber: LiveData<ResultByCoroutine<UpdateAppBean>> = _versionNumber

    fun getUpdateVersion() {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
            val mUpdateNumber = networkRequestManager.fetchResult({
                apiService.getLatestVersion()
            }, UpdateAppBean::class.java, "getUpdateVersion")
            _versionNumber.value = mUpdateNumber
            } finally {
                _loading.postValue(false)
            }
        }
    }

    //下载更新
    private val _downloadProgress = MutableLiveData<ResultByCoroutine<Int>>()
    val downloadProgress: LiveData<ResultByCoroutine<Int>> = _downloadProgress
    private val _downloadComplete = MutableLiveData<ResultByCoroutine<String>>()
    val downloadComplete: LiveData<ResultByCoroutine<String>> = _downloadComplete

    fun downloadUpdate(urlString: String) {
        viewModelScope.launch {
            try {
                // 确保下载操作在 IO 线程中进行
                val filePath = withContext(Dispatchers.IO) {
                    downloadFile(urlString)
                }
                _downloadComplete.postValue(ResultByCoroutine.Success(filePath))
            } catch (e: Exception) {
                _downloadComplete.postValue(ResultByCoroutine.Error(e))
                LogUtils.e("Error downloading update", e)
            }
        }
    }

    //下载文件
    private fun downloadFile(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        val fileLength = connection.contentLength
        LogUtils.e("File length: $fileLength")

        val outputFile = File(Environment.getExternalStorageDirectory(), "app-debug.apk")
        LogUtils.e("Saving file to: " + outputFile.absolutePath)

        try {
            connection.inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while ((input.read(data).also { count = it }) != -1) {
                        total += count.toLong()
                        output.write(data, 0, count)

                        // 更新下载进度
                        val progress = (total * 100 / fileLength).toInt()
                        LogUtils.e("Download progress: $progress%")
                        _downloadProgress.postValue(ResultByCoroutine.Success(progress))
                    }
                    LogUtils.e("Download completed, total bytes: $total")
                }
            }
        } finally {
            connection.disconnect()
        }

        return outputFile.absolutePath
    }
}
