package com.tanjinc.autotool

import android.content.Context

/**
 * Author by tanjincheng, Date on 18-11-26.
 */
class SharePreferenceUtil {
    companion object {
        fun putBoolean(key:String, value: Boolean) {
            val sp  = MyApplication.getApplication().getSharedPreferences("auto_tool", Context.MODE_PRIVATE).edit()
            when(value) {
                is Boolean -> sp.putBoolean(key, value)
            }
            sp.apply()
        }

        fun getBoolean(key: String) : Boolean {
            val sp  = MyApplication.getApplication().getSharedPreferences("auto_tool", Context.MODE_PRIVATE)
            return sp.getBoolean(key, true)
        }
    }
}