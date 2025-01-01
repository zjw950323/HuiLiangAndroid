package com.huiliang.picturesque.api

import com.huiliang.picturesque.bean.AIPicResponseBean
import com.huiliang.picturesque.bean.UpdateAppBean
import com.huiliang.lib_net.bean.ApiListResponse
import com.huiliang.lib_net.bean.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
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

    //先查询数据库 没有再生成
    @GET("get_image")
    suspend fun getImageList(@QueryMap params: WeakHashMap<String, Any>): Response<ApiListResponse<AIPicResponseBean>>

    //调用后台接口生成图片
    @GET("get_image_generate")
    suspend fun getImageGenerate(@QueryMap params: WeakHashMap<String, Any>): Response<ApiListResponse<AIPicResponseBean>>

    //获取最新版本号
    @GET("get_latest_version")
    suspend fun getLatestVersion(): Response<ApiResponse<UpdateAppBean>>
}