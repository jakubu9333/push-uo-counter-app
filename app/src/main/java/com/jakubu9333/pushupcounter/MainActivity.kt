package com.jakubu9333.pushupcounter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent

import androidx.annotation.RequiresApi
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.jakubu9333.pushupcounter.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VEL_THRESHOLD = 100

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onFling(
            downEvent: MotionEvent?,
            moveEvent: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = moveEvent?.x?.minus(downEvent!!.x) ?: 0.0f

            if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VEL_THRESHOLD) {
                if (diffX > 0) {
                    //r
                    this@MainActivity.onSwipeRight()
                } else {
                    //l
                    this@MainActivity.onSwipeLeft()
                }
                return true
            }

            return super.onFling(downEvent, moveEvent, velocityX, velocityY)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (navFragment == null) {
            navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        }
        return if (event != null && !detector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSwipeRight() {
        val fragment= navFragment?.childFragmentManager?.fragments?.get(0)
        if (fragment is DayFragment){
            val dayFragment:DayFragment=fragment
            dayFragment.prevDay()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSwipeLeft() {
        val fragment= navFragment?.childFragmentManager?.fragments?.get(0)
        if (fragment is DayFragment){
            val dayFragment:DayFragment=fragment
            dayFragment.nextDay()
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var navFragment: Fragment? = null
    private lateinit var detector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        createNotificationChannel()
        super.onCreate(savedInstanceState)
        detector = GestureDetectorCompat(this, GestureListener())
        navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> settings()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun settings(): Boolean {

        navController.navigate(R.id.action_DayFragment_to_settingsFragment)
        return true
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "notifications"
            val descriptionText = "notifications"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("my_channel_01", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}