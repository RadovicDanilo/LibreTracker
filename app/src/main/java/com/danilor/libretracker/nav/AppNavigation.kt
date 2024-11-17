package com.danilor.libretracker.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danilor.libretracker.view.ExcludePackagesView
import com.danilor.libretracker.view.ScreenTimeUI

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.ScreenTime) {
        composable(Routes.ScreenTime) {
            ScreenTimeUI { navController.navigate(Routes.ExcludedPackages) }
        }
        composable(Routes.ExcludedPackages) {
            ExcludePackagesView(context = LocalContext.current) { navController.navigate(Routes.ScreenTime) }
        }
    }
}