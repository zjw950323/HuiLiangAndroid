package com.huiliang.aidemo.ui

import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.huiliang.aidemo.R
import com.huiliang.aidemo.databinding.ActivityMainBinding
import com.huiliang.aidemo.vm.AiViewModel
import com.huiliang.lib_base.ui.BaseActivity

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
        mViewBinding.apply {

            NavigationUI.setupWithNavController(bottomNavigationView, navController)

            // 监听 NavController 的变化，自动更新底部导航栏的选中项
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.vincentDiagramFragment -> bottomNavigationView.menu.findItem(R.id.vincentDiagramFragment).isChecked =
                        true

                    R.id.mapStorageFragment -> bottomNavigationView.menu.findItem(R.id.mapStorageFragment).isChecked =
                        true
                }
            }
            // 初始化时，设置默认选中的图标
            val currentDestination = navController.currentDestination
            if (currentDestination?.id == R.id.vincentDiagramFragment) {
                bottomNavigationView.menu.findItem(R.id.vincentDiagramFragment).isChecked = true
            } else if (currentDestination?.id == R.id.mapStorageFragment) {
                bottomNavigationView.menu.findItem(R.id.mapStorageFragment).isChecked = true
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}
