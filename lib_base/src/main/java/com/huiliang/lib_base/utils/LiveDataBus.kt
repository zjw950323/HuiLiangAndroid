package com.huiliang.lib_base.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Time: 2024/6/14
 * Author: Muse丶
 * Description:
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
// LiveDataBus 是一个事件总线，通过 LiveData 实现
object LiveDataBus {
    // 存放不同事件类型的订阅者
    private val bus: MutableMap<String, BusMutableLiveData<Any>> = HashMap()

    // 获取特定事件类型的 LiveData
    @Suppress("UNCHECKED_CAST")
    @Synchronized
    fun <T> with(key: String, type: Class<T>): BusMutableLiveData<T> {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData()
        }
        return bus[key] as BusMutableLiveData<T>
    }

    // 获取通用类型的 LiveData
    fun with(key: String): BusMutableLiveData<Any> {
        return with(key, Any::class.java)
    }

    // 自定义的 MutableLiveData，避免在订阅之前收到事件
    class BusMutableLiveData<T> : MutableLiveData<T>() {

        // 重写 observe 方法，增加 hook 调用
        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            hook(observer)
        }

        // 使用反射技术，避免在订阅之前收到事件
        private fun hook(observer: Observer<in T>) {
            val liveDataClass = LiveData::class.java
            try {
                // 获取 LiveData 的 mObservers 字段
                val field = liveDataClass.getDeclaredField("mObservers")
                field.isAccessible = true
                val mObservers = field.get(this)
                val mObserversClass = mObservers::class.java

                // 获取 mObservers 的 get 方法
                val method = mObserversClass.getDeclaredMethod("get", Any::class.java)
                method.isAccessible = true
                val objectWrapperEntry = method.invoke(mObservers, observer)

                // 获取 ObserverWrapper 对象
                var objectWrapper: Any? = null
                if (objectWrapperEntry is Map.Entry<*, *>) {
                    objectWrapper = objectWrapperEntry.value
                }
                if (objectWrapper == null) {
                    throw NullPointerException("Wrapper cannot be null")
                }

                // 获取 ObserverWrapper 的 mLastVersion 字段
                val wrapperClass = objectWrapper.javaClass.superclass
                val mLastVersion = wrapperClass.getDeclaredField("mLastVersion")
                mLastVersion.isAccessible = true

                // 获取 LiveData 的 mVersion 字段
                val mVersion = liveDataClass.getDeclaredField("mVersion")
                mVersion.isAccessible = true

                // 将 mVersion 的值设置为 mLastVersion
                val mVersionValue = mVersion.get(this)
                mLastVersion.set(objectWrapper, mVersionValue)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}