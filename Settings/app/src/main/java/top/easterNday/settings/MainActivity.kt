package top.easterNday.settings

import android.os.Bundle
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import top.easterNday.settings.databinding.ActivityMainBinding
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

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
    }

    // 菜单创建
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_about, menu)
        return true
    }

    // Navigation back
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