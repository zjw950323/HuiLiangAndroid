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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 * Time: 2024/6/14
 * Author: Muse丶
 * Description:BaseMVVMLazyByContextFragment
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
abstract class BaseLazyByFragment<T : ViewModel, M : ViewDataBinding> : Fragment() {

    // 根视图
    protected var mRoot: View? = null

    // 标示是否第一次初始化数据
    protected var mIsFirstInitData = true

    // ViewModel实例
    lateinit var mViewModel: T

    // ViewDataBinding实例
    lateinit var mViewBinding: M

    // 标示Fragment是否可见
    private var isFragmentVisible = false

    // 标示View是否已创建
    private var isViewCreated = false

    // 当Fragment附加到Activity时调用，用于初始化参数
    override fun onAttach(context: Context) {
        super.onAttach(context)
        initArgs(arguments)
    }

    // 创建视图并初始化数据绑定
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mRoot == null) {
            // 获取布局ID
            val layId = getContentLayoutId()
            // 初始化ViewDataBinding
            mViewBinding = DataBindingUtil.inflate(inflater, layId, container, false)
            // 获取根视图
            val root = mViewBinding.root
            // 初始化控件
            initWidget(root)
            // 设置生命周期所有者
            mViewBinding.lifecycleOwner = this
            // 设置根视图
            mRoot = root
        } else {
            // 如果根视图已经有父视图，则将其移除
            if (mRoot!!.parent != null) {
                (mRoot!!.parent as ViewGroup).removeView(mRoot)
            }
        }

        isViewCreated = true
        if (isFragmentVisible) {
            lazyLoad()
        }

        return mRoot
    }

    // 初始化ViewModel
    private fun initViewModel() {
        val factory = getViewModelFactory()
        val types = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        mViewModel = if (factory != null) {
            ViewModelProvider(this, factory).get(types[0] as Class<T>)
        } else {
            ViewModelProvider(this).get(types[0] as Class<T>)
        }
    }

    // 当视图创建完成时调用，用于初始化数据
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mIsFirstInitData) {
            mIsFirstInitData = false
            onFirstInit()
        }
        bindClickListener()
        initViewModel()
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (!isFragmentVisible) {
                    isFragmentVisible = true
                    if (isViewCreated) {
                        lazyLoad()
                    }
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                isFragmentVisible = false
            }
        })
    }

    // 懒加载方法，只有在Fragment可见且视图已创建时调用
    private fun lazyLoad() {
        if (mIsFirstInitData) {
            return
        }
        initData()
    }

    // 绑定点击事件，可在子类中实现
    open fun bindClickListener() {}

    // 初始化相关参数，可在子类中实现
    open fun initArgs(bundle: Bundle?) {}

    // 获取布局ID，子类必须实现
    @LayoutRes
    abstract fun getContentLayoutId(): Int

    // 绑定ViewModel，子类必须实现
    abstract fun bindViewModel()

    // 初始化控件，可在子类中实现
    open fun initWidget(root: View) {}

    // 初始化数据，可在子类中实现
    open fun initData() {
        bindViewModel()
    }

    // 当首次初始化数据时调用的方法，可在子类中实现
    open fun onFirstInit() {}

    // 返回键触发时调用的方法，可在子类中实现
    open fun onBackPressed(): Boolean {
        return false
    }

    // 获取 ViewModel 工厂方法，子类可以实现
    open fun getViewModelFactory(): ViewModelProvider.Factory? {
        return context?.let { DefaultViewModelFactory(it) }
    }

    // 默认的 ViewModelProvider.Factory 实现
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