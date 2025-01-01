package com.huiliang.picturesque.bean

/**
 * Time: 2024/12/12
 * Author: muse
 * QQ: 554953278
 * Description:文生图返回值
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
data class AIPicResponseBean(
    //图片名称
    val image_name: String,
    //文字描述
    val image_description: String,
    //选用的模型
    val image_model: String,
    //分辨率
    val image_size: String
)
