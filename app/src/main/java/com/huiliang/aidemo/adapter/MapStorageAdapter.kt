package com.huiliang.aidemo.adapter

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.huiliang.aidemo.R
import com.huiliang.aidemo.bean.AIPicResponseBean
import com.huiliang.lib_base.utils.ImageSaver
import com.huiliang.lib_net.SimpleConstant
import com.xinlicheng.lib_base.adapter.BaseAdapter

/**
 * Time: 2024/12/17
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
class MapStorageAdapter(mContext: Context) :
    BaseAdapter<AIPicResponseBean, MapStorageAdapter.ViewHolder>(mContext) {
    private var sizeColor: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(mContext)
            .inflate(R.layout.adapter_map_storage, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (dataList.isNotEmpty()) {
            val data = dataList[position]
            holder.apply {
                val nightModeFlags: Int =
                    mContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    // 状态栏文字颜色变为白色
                    sizeColor = mContext.resources.getColor(R.color.white)

                } else {
                    // 状态栏文字颜色变为黑色
                    sizeColor = mContext.resources.getColor(R.color.black)
                }
                // 状态栏文字颜色变为黑色
                tvDescription.apply {
                    setTextColor(sizeColor)
                    text = "描述：${data.image_description}"
                }
                tvModel.apply {
                    setTextColor(sizeColor)
                    text = "模型：${data.image_model}"
                }
                tvSize.apply {
                    setTextColor(sizeColor)
                    text = "分辨率：${data.image_size}"
                }
               val url = SimpleConstant().BASE_PIC_URL + data.image_name
                Glide.with(mContext).load(url).placeholder(R.drawable.placeholder)
                    .error(R.drawable.error).into(ivPic)
                btSave.setOnClickListener {
                    val picUrl = SimpleConstant().BASE_PIC_URL + data.image_name
                    val fileName = "network_image_${System.currentTimeMillis()}.jpg" // 自定义文件名
                    // 保存图片到相册
                    ImageSaver.saveNetworkImageToGallery(mContext, picUrl, fileName)
                }
            }

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPic: ImageView = itemView.findViewById(R.id.iv_pic)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvModel: TextView = itemView.findViewById(R.id.tv_model)
        val tvSize: TextView = itemView.findViewById(R.id.tv_size)
        val btSave: ConstraintLayout = itemView.findViewById(R.id.bt_save)
    }

}