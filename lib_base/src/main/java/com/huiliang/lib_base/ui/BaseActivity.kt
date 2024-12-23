package com.huiliang.lib_base.ui


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
//import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.huiliang.lib_base.utils.AppManager
import com.huiliang.lib_base.utils.StatusBarUtil
import java.lang.reflect.ParameterizedType

/**
 * Time: 2024/6/14
 * Author: Muse丶
 * Description:BaseMVVMByContextActivity
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
abstract class BaseActivity<T : ViewModel, M : ViewDataBinding> : AppCompatActivity() {
    lateinit var mViewModel: T
    lateinit var mViewBinding: M

    //    val httpReceiver: BroadcastReceiver by lazy { HttpReceiver2() }
    var isLogin = false
    var needLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWidows()

        if (initArgs(intent.extras)) {
            mViewBinding = DataBindingUtil.setContentView(this, initContentLayoutId())

            initBefore()
            initWidget()

        } else {
            finish()
        }
        StatusBarUtil.transparencyBar(this) // 透明状态栏
        val nightModeFlags: Int =
            this.getResources().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // 状态栏文字颜色变为白色
            BarUtils.setStatusBarLightMode(this, false)
        } else {
            // 状态栏文字颜色变为黑色
            BarUtils.setStatusBarLightMode(this, true)
        }

        initViewModel()
        initData()
        mViewBinding.lifecycleOwner = this
        AppManager.instance.addActivity(this)

//        ARouter.getInstance().inject(this) // 当ARouter框架跳转时接收参数页面需要这行代码

        bindClickListener()
        LogUtils.e(this.javaClass.simpleName)
//        if (needLogin) {
//            LocalBroadcastManager.getInstance(this)
//                .registerReceiver(httpReceiver, IntentFilter("needLogin"))
//        }
    }

//    inner class HttpReceiver2 : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            if (isLogin) {
//                // 已登录
//            } else {
//                if (needLogin) {
//                    val tag = intent.getIntExtra("tag", 0)
//                    showLoginActivity(context)
//                    close1()
//                }
//            }
//        }
//    }

    fun close1() {
        finish()
    }

    @SuppressLint("NewApi")
    private fun initViewModel() {
        val factory = getViewModelFactory()
        val types = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        mViewModel = if (factory != null) {
            ViewModelProvider(this, factory)[types[0] as Class<T>]
        } else {
            ViewModelProvider(this)[types[0] as Class<T>]
        }
    }

    open fun initWidows() {}

    open fun initArgs(extras: Bundle?): Boolean {
        return true
    }

    @LayoutRes
    abstract fun initContentLayoutId(): Int

    open fun initBefore() {}

    open fun bindClickListener() {}

    open fun initWidget() {
//        ARouter.getInstance().inject(this)
    }

    open fun initData() {}

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        // 得到当前Activity下的所有Fragment
        val fragments = supportFragmentManager.fragments
        // 判断是否为空
        if (fragments.isNotEmpty()) {
            for (fragment in fragments) {
                // 判断是否为我们能够处理的Fragment类型
                if (fragment is BaseFragment<*, *>) {
                    // 判断是否拦截了返回按钮
                    if (fragment.onBackPressed()) {
                        // 如果有直接Return
                        return
                    }
                } else if (fragment is BaseLazyByFragment<*, *>) {
                    // 判断是否拦截了返回按钮
                    if (fragment.onBackPressed()) {
                        // 如果有直接Return
                        return
                    }
                }
            }
        }

        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
//        LocalBroadcastManager.getInstance(AppGlobals.getApplication())
//            .unregisterReceiver(httpReceiver)
        AppManager.instance.finishActivity(this)
    }

    open fun getViewModelFactory(): ViewModelProvider.Factory? {
        return DefaultViewModelFactory(this)
    }

    class DefaultViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return try {
                val constructor = modelClass.getConstructor(Context::class.java)
                constructor.newInstance(context)
            } catch (e: Exception) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
    }
}
