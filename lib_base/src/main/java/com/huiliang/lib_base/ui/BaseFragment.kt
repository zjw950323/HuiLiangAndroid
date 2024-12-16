package com.huiliang.lib_base.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 * Time: 2024/6/14
 * Author: Muse丶
 * Description:BaseMVVMByContextFragment
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
abstract class BaseFragment<T : ViewModel, M : ViewDataBinding> : Fragment() {
    protected var mRoot: View? = null

    // 标示是否第一次初始化数据
    protected var mIsFirstInitData = true
    lateinit var mViewModel: T
    lateinit var mViewBinding: M

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initArgs(arguments)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mRoot == null) {
            val layId = getContentLayoutId()
            // 初始化当前的根布局，但不在创建时就添加到container里边
            mViewBinding = DataBindingUtil.inflate(inflater, layId, container, false)

            val root = mViewBinding.root
            initWidget(root)
            mViewBinding.lifecycleOwner = this
            mRoot = root
        } else {
            if (mRoot!!.parent != null) {
                // 把当前Root从其父控件中移除
                (mRoot!!.parent as ViewGroup).removeView(mRoot)
            }
        }

        return mRoot
    }

    private fun initViewModel() {
        val factory = getViewModelFactory()
        val types = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        mViewModel = if (factory != null) {
            ViewModelProvider(this, factory).get(types[0] as Class<T>)
        } else {
            ViewModelProvider(this).get(types[0] as Class<T>)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mIsFirstInitData) {
            // 触发一次以后就不会触发
            mIsFirstInitData = false
            // 触发
            onFirstInit()
        }
        bindClickListener()
        initViewModel()
        initData()

    }

    open fun bindClickListener() {
    }

    /**
     * 初始化相关参数
     */
    open fun initArgs(bundle: Bundle?) {

    }

    /**
     * 得到当前界面的资源文件Id
     *
     * @return 资源文件Id
     */
    @LayoutRes
    abstract fun getContentLayoutId(): Int

    /**
     * MVVM下数据一般在viewModel中 需要绑定
     *
     * @return 资源文件Id
     */
    abstract fun bindViewModel()

    /**
     * 初始化控件
     */
    open fun initWidget(root: View) {
    }

    /**
     * 初始化数据
     */
    open fun initData() {
        bindViewModel()
    }

    /**
     * 当首次初始化数据的时候会调用的方法
     */
    open fun onFirstInit() {

    }

    /**
     * 返回按键触发时调用
     *
     * @return 返回True代表我已处理返回逻辑，Activity不用自己finish。
     * 返回False代表我没有处理逻辑，Activity自己走自己的逻辑
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    /**
     * 获取 ViewModel 工厂方法，子类可以实现
     */
    open fun getViewModelFactory(): ViewModelProvider.Factory? {
        return context?.let { DefaultViewModelFactory(it) }
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

