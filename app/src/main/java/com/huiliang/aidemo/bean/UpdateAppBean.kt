package com.huiliang.aidemo.bean

/**
 * Time: 2024/12/18
 * Author: muse
 * QQ: 554953278
 * Description:获取新版本号
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
data class UpdateAppBean(
    //版本号
    val version_number: String,
    //更新描述
    val update_description: String,
    //是否强制更新 0:false 1:true
    val is_force_update: Int,
    //更新地址
    val update_url: String
)
