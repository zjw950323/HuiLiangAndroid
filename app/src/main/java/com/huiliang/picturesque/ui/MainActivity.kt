package com.huiliang.picturesque.ui

import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.huiliang.lib_base.ui.BaseActivity
import com.huiliang.picturesque.R
import com.huiliang.picturesque.databinding.ActivityMainBinding
import com.huiliang.picturesque.vm.AiViewModel

/**
 * Time: 2024/11/18
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
class MainActivity : BaseActivity<AiViewModel, ActivityMainBinding>() {

    override fun initContentLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        super.initData()

        val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)

        // 设置底部导航与NavController关联
        mViewBinding.apply {
            NavigationUI.setupWithNavController(bottomNavigationView, navController)

            // 初始化时，设置底部导航的选中项
            val currentDestination = navController.currentDestination
            currentDestination?.let { destination ->
                bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
            }

            // 监听NavController的目的地变化，自动更新底部导航栏的选中项
            navController.addOnDestinationChangedListener { _, destination, _ ->
                bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // 通过NavController管理Fragment的状态，避免手动操作Fragment生命周期
    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        if (!navController.navigateUp()) {
            super.onBackPressed()  // 默认处理返回事件
        }
    }
}