package fr.mastergime.meghasli.escapegame.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.ActivityMainBinding
import fr.mastergime.meghasli.escapegame.ui.fragments.SplashFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val splashFragment = SplashFragment()

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.toolBar)

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.logFragment,
            R.id.splashFragment,
        )
            .build()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navHostFragment.findNavController().run {
            _binding.toolBar.setupWithNavController(this, appBarConfiguration)
        }

        _binding.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = this.resources.getColor(R.color.gameColor, theme)
        }

    }


}