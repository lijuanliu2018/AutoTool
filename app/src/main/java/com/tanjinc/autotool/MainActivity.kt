package com.tanjinc.autotool

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    val REQUEST_MEDIA_PROJECTION = 18


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBtn.setOnClickListener {
//            requestPermission()
//            startService((Intent(this, WorkService::class.java)))
            intent.setClassName("com.jifen.qukan", "com.jifen.qkbase.main.MainActivity")
            startActivity(intent)
            sendBroadcast(Intent("StartWork"))

        }

        settingBtn.setOnClickListener {
            Toast.makeText(this, "click!!", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "click ==")
            jumpToSettingPage(this)
        }

        testBtn.setOnClickListener {
            Toast.makeText(this, "aaaaaa", Toast.LENGTH_SHORT).show()
        }
        qiandaoBtn.setOnClickListener {

            SharePreferenceUtil.putSharePreference(Constants.QIANDAO_TASK, true)
            putSharePreference("qiandao_task", true)
            intent.setClassName("com.jifen.qukan", "com.jifen.qkbase.main.MainActivity")
            startActivity(intent)
        }
    }


    private fun putSharePreference(key:String, value: Any) {
        val sp  = getSharedPreferences("auto_tool", Context.MODE_PRIVATE).edit()
        when(value) {
            is Boolean -> sp.putBoolean(key, value)
            is Int -> sp.putInt(key, value)
            is String -> sp.putString(key, value)
        }
        sp.apply()
    }

    private fun getSharePreference(key: String) : Any {
        val sp  = getSharedPreferences("auto_tool", Context.MODE_PRIVATE)
        return sp.getBoolean(key, true)
    }

    private fun requestPermission() {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_MEDIA_PROJECTION ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    FloatWindowsService.Companion.setResultData(data)
                    startService(Intent(applicationContext, FloatWindowsService::class.java))
                }
        }

    }

    fun jumpToSettingPage(context: Context) {
        try {


            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        } catch (ignore: Exception) {
        }

    }
}
