package com.huiliang.aidemo.api

import com.huiliang.aidemo.bean.AIPicResponseBean
import com.huiliang.aidemo.bean.UploadPicResponse
import com.huiliang.aidemo.bean.VincentDiagramResponse
import com.huiliang.lib_net.bean.ApiListResponse
import com.huiliang.lib_net.bean.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.QueryMap
import java.util.WeakHashMap


/**
 * Time: 2024/11/18
 * Author: muse
 * QQ: 554953278
 * Description:
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
interface AiApi {
//    @POST("v1/images/generations")
//    suspend fun vincentDiagram(
//        @Body requestBody: RequestBody
//    ): Response<ApiListResponse<VincentDiagramResponse>>
//
//    @Multipart
//    @POST("upload")  // 你的上传接口地址
//    suspend fun uploadFile(
//        @Part file: MultipartBody.Part
//    ): Response<ApiResponse<UploadPicResponse>>

    //先查询数据库 没有再生成
    @GET("get_image")
    suspend fun getImageList(@QueryMap params: WeakHashMap<String, Any>): Response<ApiListResponse<AIPicResponseBean>>

    //调用后台接口生成图片
    @GET("get_image_generate")
    suspend fun getImageGenerate(@QueryMap params: WeakHashMap<String, Any>): Response<ApiListResponse<AIPicResponseBean>>
}