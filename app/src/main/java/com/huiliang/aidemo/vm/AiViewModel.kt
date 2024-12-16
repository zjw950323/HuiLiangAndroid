package com.huiliang.aidemo.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.huiliang.aidemo.api.AiApi
import com.huiliang.aidemo.bean.AIPicResponseBean
import com.huiliang.lib_net.NetworkRequestManager
import com.huiliang.lib_net.ResultByCoroutine
import com.huiliang.lib_net.vm.BaseViewModel
import kotlinx.coroutines.launch
import java.util.WeakHashMap

class AiViewModel(context: Context) : BaseViewModel(context) {
    private val networkRequestManager = NetworkRequestManager.getInstance(context)
    private val apiService = networkRequestManager.createService(AiApi::class.java)
//    private val uploadPicService =
//        networkRequestManager.createService(AiApi::class.java, SimpleConstant().UPLOAD_PIC_URL)

//    private val mContext = context

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
            val mAiPictureInfo = networkRequestManager.fetchListResult({
                apiService.getImageList(map)
            }, object : TypeToken<List<AIPicResponseBean>>() {}, "getAIPicture")
            _aiPictureInfoData.value = mAiPictureInfo
            _loading.postValue(false)
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
            val mAiPictureGenerateInfo = networkRequestManager.fetchListResult({
                apiService.getImageGenerate(map)
            }, object : TypeToken<List<AIPicResponseBean>>() {}, "getImageGenerate")
            _aiPictureGenerateData.value = mAiPictureGenerateInfo
            _loading.postValue(false)
        }
    }

//    //文生图
//    private val _openAiPictureInfoData =
//        MutableLiveData<ResultByCoroutine<List<VincentDiagramResponse>>>()
//    val openAiPictureInfoData: LiveData<ResultByCoroutine<List<VincentDiagramResponse>>> =
//        _openAiPictureInfoData
//
//    fun getOpenAIPicture(description: String) {
//        val map = WeakHashMap<String, Any>()
//        map["prompt"] = description
//        map["n"] = 1
//        map["model"] = "dall-e-3"
//        map["size"] = "1024x1024"
//        val json = Gson().toJson(map)
//        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json)
//        viewModelScope.launch {
//            _loading.postValue(true)
//            val mOpenAiPictureInfo = networkRequestManager.fetchListResult({
//                apiService.vincentDiagram(body)
//            }, object : TypeToken<List<VincentDiagramResponse>>() {}, "getOpenAIPicture")
//            _openAiPictureInfoData.value = mOpenAiPictureInfo
//            _loading.postValue(false)
//        }
//    }

//    fun downloadImage(url: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val client = OkHttpClient()
//            val request = Request.Builder().url(url).build()
//            try {
//                // 使用同步请求
//                val response: Response = client.newCall(request).execute()
//
//                if (response.isSuccessful) {
//                    val inputStream: InputStream? = response.body?.byteStream()
//                    if (inputStream != null) {
//                        val file = createImageFile()
//                        if (file != null) {
//                            saveImageToFile(inputStream, file)
//                            uploadFile(file)
//                        }
//                    }
//                } else {
//                    LogUtils.e("DOWNLOAD", "Download failed: ${response.message}")
//                    withContext(Dispatchers.Main) {
//                        ToastUtils.showShort("Download failed")
//                    }
//                }
//            } catch (e: IOException) {
//                LogUtils.e("DOWNLOAD", "Error in downloading image", e)
//                withContext(Dispatchers.Main) {
//                    ToastUtils.showShort("Error in downloading image")
//                }
//            }
//        }
//    }

//    private fun createImageFile(): File? {
//        // 获取存储路径
//        val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyApp")
//        } else {
//            File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "MyApp"
//            )
//        }
//
//        if (!storageDir.exists()) {
//            storageDir.mkdirs()
//        }
//        // 获取当前时间（年月日时分秒）
//        val currentDate = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
//        // 生成随机数
//        val randomNum = Random.nextInt(1000, 9999)
//
//        // 创建文件名：image_当前时间_随机数.jpg
//        val randomFileName = "image_${currentDate}_$randomNum.jpg"
//        val file = File(storageDir, randomFileName)
//
//        return if (!file.exists()) {
//            file.createNewFile()
//            file
//        } else {
//            file
//        }
//    }

//    private suspend fun saveImageToFile(inputStream: InputStream, file: File) {
//        try {
//            val outputStream: OutputStream = FileOutputStream(file)
//            val buffer = ByteArray(1024)
//            var length: Int
//            while (inputStream.read(buffer).also { length = it } != -1) {
//                outputStream.write(buffer, 0, length)
//            }
//            outputStream.flush()
//
//            // 显示保存成功消息
//            withContext(Dispatchers.Main) {
//                ToastUtils.showShort("Image saved to ${file.absolutePath}")
//            }
//        } catch (e: IOException) {
//            LogUtils.e("DOWNLOAD", "Error saving image", e)
//            withContext(Dispatchers.Main) {
//                ToastUtils.showShort("Failed to save image")
//            }
//        } finally {
//            inputStream.close()
//        }
//    }

//    private val _uploadPicData = MutableLiveData<ResultByCoroutine<UploadPicResponse>>()
//    val uploadPicData: LiveData<ResultByCoroutine<UploadPicResponse>> = _uploadPicData

//    // 上传图片到服务器
//    @Throws(IOException::class)
//    fun uploadFile(file: File) {
//        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
//
//        // 创建 MultipartBody.Part，表示文件部分
//        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
//        viewModelScope.launch {
//            _loading.postValue(true)
//            val mUploadPic = networkRequestManager.fetchResult({
//                uploadPicService.uploadFile(body)
//            }, UploadPicResponse::class.java, "uploadFile")
//            _uploadPicData.value = mUploadPic
//            _loading.postValue(false)
//        }
//    }
}
