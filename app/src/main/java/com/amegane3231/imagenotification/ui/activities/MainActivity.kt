package com.amegane3231.imagenotification.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.ui.fragments.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setContent {
//            val navController = rememberNavController()
//            NavHost(navController, R.id.homeFragment.toString()) {
//                composable(R.id.homeFragment.toString()) { CreateNavigation(navController) }
//            }
//            navController.navigate(R.id.homeFragment) {
//                popUpTo()
//            }
//        }
    }

    @Composable
    private fun CreateNavigation(navController: NavController) {
        Column {
            Text(text = "Hello World!")
            Button(onClick = { navController.navigate(R.id.homeFragment) }) {

            }
        }
    }
}