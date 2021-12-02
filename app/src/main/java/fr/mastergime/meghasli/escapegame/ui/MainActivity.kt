package fr.mastergime.meghasli.escapegame.ui

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnSystemUiVisibilityChangeListener
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.ActivityMainBinding
import fr.mastergime.meghasli.escapegame.services.MediaPlayerSerivce
import fr.mastergime.meghasli.escapegame.services.MediaPlayerSerivce_ProvidesMediaPlayerFactory
//import fr.mastergime.meghasli.escapegame.services.MediaPlayerSerivce
import fr.mastergime.meghasli.escapegame.ui.fragments.SplashFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
//
    private lateinit var _binding: ActivityMainBinding
    private val splashFragment = SplashFragment()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var decorView: View

    private var backPressedTime = 0L

    @Inject
    lateinit var mediaPlayerFactory: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        decorView = window.decorView

        disableStatusBar()
        configActionBar()
        setUpBackPressedSystem()
        initMedia()

        @Suppress("DEPRECATION")
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility == 0) {
                decorView.systemUiVisibility = hideSystemBars()
            }
        }

    }

    fun configActionBar() {
        setSupportActionBar(_binding.toolBar)
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.logFragment,
            R.id.splashFragment,
            R.id.menuFragment,
            R.id.creatSessionFragment,
            R.id.signUpFragment,
            R.id.joinSessionFragment,
            R.id.sessionRoomFragment,
            R.id.gameFragment,
            R.id.enigme1Fragment,
            R.id.enigme21Fragment,
            R.id.noSupportedNFC
        )
            .build()
    }

    @Suppress("DEPRECATION")
    private fun disableStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    fun setUpBackPressedSystem() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navHostFragment.findNavController().run {
            _binding.toolBar.setupWithNavController(this, appBarConfiguration)
        }

        _binding.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun initMedia() {
        //mediaPlayerFactory.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerFactory.pause()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    @Suppress("DEPRECATION")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            decorView.systemUiVisibility = hideSystemBars()
        }
    }

    @Suppress("DEPRECATION")
    fun hideSystemBars(): Int {
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
           finish()
        } else {
            Toast.makeText(applicationContext, "Press Back Again To Exit App", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

}