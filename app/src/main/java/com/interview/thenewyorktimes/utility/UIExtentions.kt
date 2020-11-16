package com.interview.thenewyorktimes.utility

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.text.format.DateUtils
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.ConnectivityManagerCompat
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.interview.thenewyorktimes.ui.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by akshay on 14,November,2020
 * akshay2211@github.io
 */

fun BottomNavigationView.setNavigation(navController: NavController) {
    NavigationUI.setupWithNavController(
        this,
        navController
    )
}


/**
 * extension [setUpStatusNavigationBarColors] to setup color codes
 * and themes according to themes
 */
fun Window.setUpStatusNavigationBarColors(isLight: Boolean = false, colorCode: Int = Color.WHITE) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        statusBarColor = colorCode
        navigationBarColor = colorCode
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        setDecorFitsSystemWindows(isLight)
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = if (isLight) {
            0
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                0
            }
        }
    }
}

/**
 * extension [isDarkThemeOn] checks the saved theme from preference
 * and returns boolean
 */
fun Context.isDarkThemeOn(): Boolean {
    var key = PreferenceManager.getDefaultSharedPreferences(this).getString("list_theme", "1")
    return when (key) {
        "2" -> true
        "1" -> false
        else -> return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

}

fun String?.timeAgo(): CharSequence? {
    if (this.isNullOrEmpty()) {
        return ""
    }
    //2020-11-14T05:00:17-05:00
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
    var date = inputFormat.parse(this)
    return DateUtils.getRelativeTimeSpanString(
        date.time,
        Calendar.getInstance().timeInMillis,
        DateUtils.MINUTE_IN_MILLIS
    )
}

/**
 * extension [startSettingsActivity] starts [SettingsActivity]
 */
fun Context.startSettingsActivity() {
    startActivity(Intent(this, SettingsActivity::class.java))
}

/**
 * extension [setupTheme] calls the [AppCompatDelegate] methods which
 * setup the theme whenever the configuration is changed
 */
fun SharedPreferences?.setupTheme(key: String?, resources: Resources) {
    var value = this?.getString(key, "3")
    val def = if (resources.configuration.uiMode and
        Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    ) {
        AppCompatDelegate.MODE_NIGHT_YES

    } else {
        AppCompatDelegate.MODE_NIGHT_NO
    }

    when (value) {
        "2" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        "1" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else -> AppCompatDelegate.setDefaultNightMode(def)
    }
}

fun Context.isNetworkAvailable(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return !ConnectivityManagerCompat.isActiveNetworkMetered(cm)
}