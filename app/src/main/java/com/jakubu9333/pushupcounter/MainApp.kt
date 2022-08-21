package com.jakubu9333.pushupcounter


import android.app.Application

import com.jakubu9333.pushupcounter.database.PushUpDatabase


/**
 *
 * @author Jakub Uhlarik
 */
class MainApp: Application() {
    val database: PushUpDatabase by lazy {   PushUpDatabase.getInstance(this)}
}