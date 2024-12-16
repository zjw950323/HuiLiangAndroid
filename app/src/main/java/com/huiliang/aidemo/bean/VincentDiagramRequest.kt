package com.huiliang.aidemo.bean

/**
 * Time: 2024/11/18
 * Author: muse
 * QQ: 554953278
 * Description:请求模型
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
data class VincentDiagramRequest(
    val prompt: String,
    val n: Int,
    val model: String,
    val size: String
)
