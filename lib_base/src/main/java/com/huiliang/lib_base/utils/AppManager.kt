package com.huiliang.lib_base.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import java.util.Stack

class AppManager private constructor() {
    private val activityStack: Stack<Activity> = Stack()

    companion object {
        val instance: AppManager by lazy { AppManager() }
    }


    fun addActivity(activity: Activity) {
        activityStack.push(activity)
    }

    fun removeActivity(activity: Activity) {
        if (activityStack.contains(activity))
            activityStack.remove(activity)
    }

    fun finishActivity(activity: Activity) {
        if (!activity.isFinishing) activity.finish()
        activityStack.remove(activity)
    }

    fun finishActivity(cls: Class<*>?) {
        val it = activityStack.iterator()
        while (it.hasNext()) {
            val activity = it.next()
            if (activity.javaClass == cls) {
                if (!activity.isFinishing) activity.finish()
                it.remove()
            }
        }
    }

    /**
     * 栈顶activity
     */
    fun currentActivity(): Activity {

        return activityStack.lastElement()
    }

    /**
     * 清理栈
     */
    private fun finishAllActivity() {
        activityStack.forEach {
            it.finish()
        }
        activityStack.clear()
    }

    private var exitTime: Long = 0


    /**
     *

     */
    @SuppressLint("NewApi")
    fun exitApp(context: Context) {
        if (System.currentTimeMillis() - exitTime > 2000) {
            //App.showToast("再按一次退出程序")
            ToastUtils.showShort("再按一次退出程序")
            exitTime = System.currentTimeMillis()
        } else { //finish();
            finishAllActivity()
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appTaskList = activityManager.appTasks
            for (appTask in appTaskList) {
                appTask.finishAndRemoveTask()
            }
        }

    }
}