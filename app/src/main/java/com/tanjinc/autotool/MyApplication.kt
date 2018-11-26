package com.tanjinc.autotool

import android.app.Application

/**
 * Author by tanjincheng, Date on 18-11-26.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
    }

    companion object {
        private lateinit var application:MyApplication
        public fun getApplication():MyApplication {
            return application
        }
    }
}