package com.huiliang.aidemo.bean

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
    val image_name: String,
    val image_url: String,
    val image_path: String,
    val image_description: String,
    val image_model: String,
    val image_size: String
)
