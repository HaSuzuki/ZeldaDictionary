package com.suzukiha.zeldadictionary.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.databinding.ActivityMainBinding
import com.suzukiha.zeldadictionary.viewmodel.MainActivityModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//      ml translate 利用時に使用
//        val content: View = findViewById(android.R.id.content)
//        content.viewTreeObserver.addOnPreDrawListener(
//            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    return if (viewModel.hasLanguageModel.value == true) {
//                        if (viewModel.isShowToast) {
//                            Toast.makeText(
//                                applicationContext,
//                                getString(R.string.language_model_download_success),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                        content.viewTreeObserver.removeOnPreDrawListener(this)
//                        true
//                    } else {
//                        viewModel.checkAlreadyTranslateLanguageModel(
//                            viewModel.selectDownloadLanguage(Locale.getDefault().language)
//                        )
//                        false
//                    }
//                }
//            }
//        )
        subscribeData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun subscribeData() {
        viewModel.hasLanguageModel.observe(this) { hasLanguageModel ->
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                return@observe
            }
            if (!hasLanguageModel) {
                viewModel.downloadTranslateLanguageModelIfNeeded(Locale.getDefault().language)
            }
        }
        viewModel.availableDownloadedModel.observe(this) { available ->
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                return@observe
            }
            if (available) {
                viewModel.hasLanguageModel.postValue(true)
            }
        }
    }
}