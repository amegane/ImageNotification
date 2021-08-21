package com.amegane3231.imagenotification.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.AppLaunchState
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.databinding.ActivityMainBinding
import com.amegane3231.imagenotification.ui.compose.AppBar
import com.amegane3231.imagenotification.ui.theme.ImageNotificationTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appLaunchedState = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getInt(SharedPreferenceKey.AppLaunchedState.name, AppLaunchState.InitialLaunch.state)
        if (appLaunchedState == 0) {
            val intent = Intent(this, TutorialActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        setContent {
            val scaffoldState = rememberScaffoldState()
            ImageNotificationTheme(
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ImageNotificationTheme.colors.background
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = { AppBar(getString(R.string.app_name)) },
                        content = { AndroidViewBinding(ActivityMainBinding::inflate) }
                    )
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}