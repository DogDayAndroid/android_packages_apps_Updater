package top.easterNday.settings

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import top.easterNday.settings.DogDay.LogUtils.logger
import top.easterNday.settings.DogDay.Utils.Companion.getRealPathFromUri
import top.easterNday.settings.DogDay.Utils.Companion.installUpdate
import top.easterNday.settings.databinding.ActivityMainBinding
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var filePicker: ActivityResultLauncher<Intent>

    /**
     * 此函数设置主要活动，包括布局、操作栏、导航和后退手势处理，以及注册活动结果启动器以从设备中选择文件。
     *
     * @param savedInstanceState
     * “savedInstanceState”参数是一个“Bundle”对象，其中包含上次调用“onSaveInstanceState()”时保存的数据。它可用于在重新创建活动时恢复活动的状态。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                // CollapsingToolbarLayout completely collapsed
                //supportActionBar?.setDisplayShowTitleEnabled(true)
                supportActionBar?.setDisplayShowTitleEnabled(false)
            } else {
                // Not fully collapsed, expanded
                supportActionBar?.setDisplayShowTitleEnabled(false)
            }
        }

        // On Back Gesture, always show home & back icon
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 始终显示返回按钮
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                // 执行导航语句
                onSupportNavigateUp()
            }
        })


        /* 代码 `filePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> ... }` 正在设置一个活动结果启动器，用于从设备中选取文件。 */
        filePicker =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    val uri = data?.data
                    uri?.let { getRealPathFromUri(it)?.let { it1 -> installUpdate(it1) } }
                    logger.d("Selected file Uri: $uri")
                }
            }
    }


    /**
     * 该函数用于为 Android 应用程序中的活动创建选项菜单。
     *
     * @param menu `menu` 参数是 `Menu` 类的实例。它代表将在操作栏中显示的菜单。
     * @return 该方法返回一个布尔值，特别是“true”。
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_global, menu)
        return true
    }

    /**
     * 该函数处理菜单项的选择，并在选择“toolbar_install”项时启动文件选择器意图。
     *
     * @param item “item”参数的类型为“MenuItem”，表示用户选择的菜单项。
     * @return 该代码返回一个布尔值。
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_install -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "*/*"
                filePicker.launch(intent)
                true
            }

            else -> false

        }
    }


    /**
     * 此函数处理 Kotlin Android 应用程序中的向上导航操作，包括检查当前目的地是否是顶级目的地，如果是，则结束活动。
     *
     * @return 该方法返回一个布尔值。
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // 如果为顶级导航地址，则结束当前Activity
        val currentDestination = navController.currentDestination
        if (currentDestination != null && appBarConfiguration.topLevelDestinations.contains(currentDestination.id)) {
            finish()
            return true
        }

        val status = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return status
    }
}