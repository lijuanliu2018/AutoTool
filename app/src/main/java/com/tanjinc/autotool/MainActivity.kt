package com.tanjinc.autotool

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.tanjinc.autotool.utils.PermissionUtil
import com.tanjinc.autotool.utils.SharePreferenceUtil
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    val REQUEST_MEDIA_PROJECTION = 18
    val sAccessibilityServiceName = AutoClickService::class.java.name

    var mIsPermissionGain = false
    lateinit var mActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mActivity = this
        permissionWarmTv.setOnClickListener {
            PermissionUtil.openAccessibility(this, sAccessibilityServiceName)
        }

        startBtn.setOnClickListener {
            PermissionUtil.openAccessibility(this, AutoClickService::class.java.name)
//            requestPermission()
//            startService((Intent(this, WorkService::class.java)))
//            startQuToutiao()
//            sendBroadcast(Intent("StartWork"))
        }

        settingBtn.setOnClickListener {
            PermissionUtil.openAccessibility(this, sAccessibilityServiceName)
        }

        testBtn.setOnClickListener {

//            val isEnable = PermissionUtil.isNotificationEnabled(this)
//            Toast.makeText(this, if(isEnable) "enable" else "not enable", Toast.LENGTH_SHORT).show()
        }
        qiandaoBtn.setOnClickListener {
            if (checkAccesibilityPermission()) {
                cleanTask()
                SharePreferenceUtil.putBoolean(Constants.QIANDAO_TASK, true)
                startQuToutiao()
            }
        }
        cancelBtn.setOnClickListener {
            cleanTask()
        }

        shiwanBtn.setOnClickListener {
            if (checkAccesibilityPermission()) {
                cleanTask()
                SharePreferenceUtil.putBoolean(Constants.SHIWAN_TASK, true)
                startQuToutiao()
            }
        }
        videoBtn.setOnClickListener {
            if (checkAccesibilityPermission()) {
                cleanTask()
                SharePreferenceUtil.putBoolean(Constants.VIDEO_TASK, true)
                startQuToutiao()
            }
        }

    }


    private fun checkAccesibilityPermission() : Boolean {
        if (!mIsPermissionGain) {
            Toast.makeText(this, "没有开启辅助功能权限，请打开", Toast.LENGTH_SHORT).show()
        }
        return mIsPermissionGain
    }
    private fun cleanTask() {
        SharePreferenceUtil.putBoolean(Constants.SHIWAN_TASK, false)
        SharePreferenceUtil.putBoolean(Constants.QIANDAO_TASK, false)
        SharePreferenceUtil.putBoolean(Constants.VIDEO_TASK, false)

    }

    private fun startQuToutiao() {
        if (!checkAccesibilityPermission()) {
            return
        }
        val intent = Intent()
        intent.setClassName("com.jifen.qukan", "com.jifen.qkbase.main.MainActivity")
        startActivity(intent)
    }

    private fun requestPermission() {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION)
    }


    override fun onResume() {
        super.onResume()
        launch {
            mIsPermissionGain = PermissionUtil.isAccessibilitySettingsOn(mActivity, sAccessibilityServiceName)
            launch(UI) {
                permissionWarmTv.visibility = if (mIsPermissionGain) View.GONE else View.VISIBLE
            }
        }

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
}
