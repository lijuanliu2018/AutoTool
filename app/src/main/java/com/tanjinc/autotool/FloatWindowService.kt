package com.tanjinc.autotool

import android.annotation.TargetApi
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.os.SystemClock.sleep
import android.util.Log
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlinx.coroutines.experimental.launch


/**
 * Created by branch on 2016-5-25.
 *
 * 启动悬浮窗界面
 */
class FloatWindowsService : Service() {
    lateinit var mMediaProjectionManager: MediaProjectionManager
    lateinit var mMediaProjection: MediaProjection

    private var mVirtualDisplay: VirtualDisplay ? = null
    private var mScreenHeight:Int = 0
    private var mScreenWidth:Int = 0
    private var mScreenDensity:Int = 0
    lateinit var mImageReader:ImageReader
    lateinit var mWindowManager:WindowManager

    override fun onCreate() {
        super.onCreate()

        initWindow()

        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mMediaProjection = mMediaProjectionManager.getMediaProjection(Activity.RESULT_OK, mResultData)

        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, 0x1, 2)

        virtualDisplay()

        launch {
            while (true) {
                startCapture()
                sleep(1000)
            }

        }

    }

    private fun initWindow() {
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        mWindowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mScreenWidth = metrics.widthPixels
        mScreenHeight = metrics.heightPixels
    }

    private fun virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null)
    }

    private fun startCapture() {
        var image = mImageReader.acquireLatestImage()
        if (image == null) {
            Log.d(TAG, "image is null")
        }
        Log.d(TAG, "image success")
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    companion object {
        private var mResultData: Intent? = null
        private const val TAG = "FloatWindowService"

        fun setResultData(result: Intent) {
            mResultData = result
        }
    }
}