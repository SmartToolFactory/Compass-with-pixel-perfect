package com.smarttoolfactory.compass.libs.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager

object DisplayUtils {
    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun is7InchTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun is10InchTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE
    }

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun isInLandscapeMode(context: Context): Boolean {
        var isLandscape = false
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true
        }
        return isLandscape
    }

    fun dp2px(context: Context, dp: Int): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val displaymetrics = DisplayMetrics()
        display.getMetrics(displaymetrics)
        return (dp * displaymetrics.density + 0.5f).toInt()
    }
}