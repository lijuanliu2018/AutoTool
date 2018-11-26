package com.tanjinc.autotool

import android.content.Context

/**
 * Author by tanjincheng, Date on 18-11-26.
 */
class SharePreferenceUtil {
    companion object {
        public fun putSharePreference(key:String, value: Any) {
            val sp  = MyApplication.getApplication().getSharedPreferences("auto_tool", Context.MODE_PRIVATE).edit()
            when(value) {
                is Boolean -> sp.putBoolean(key, value)
                is Int -> sp.putInt(key, value)
                is String -> sp.putString(key, value)
            }
            sp.apply()
        }

        public fun getSharePreference(key: String) : Any {
            val sp  = MyApplication.getApplication().getSharedPreferences("auto_tool", Context.MODE_PRIVATE)
            return sp.getBoolean(key, true)
        }
    }
}