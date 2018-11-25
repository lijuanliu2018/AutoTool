package com.tanjinc.autotool

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoClickService : AccessibilityService() {
    val TAG = "AutoClickService"
    val mQutoutiaoPackage = "com.jifen.qukan" //趣头条包名
    val mTargetPackageName = "com.tanjinc.autotool"
    private lateinit var mBrocardReceiver:MyBroadcastReceiver
    private var mClickGetCoin:Boolean = false

    override fun onCreate() {
        super.onCreate()
        mBrocardReceiver = MyBroadcastReceiver()
        registerReceiver(mBrocardReceiver, IntentFilter("StartWork"))
    }
    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        PrintUtils.printEvent(event)
        if (event != null && rootInActiveWindow != null) {
            Log.d(TAG, "event type = " + event.eventType)
            when(event.packageName) {
                mQutoutiaoPackage -> {
                    Log.d(TAG, "onAccessibilityEvent " + event.text)
                    Log.d(TAG, "onAccessibilityEvent " + event.packageName)

                    val coinInfo = rootInActiveWindow.findAccessibilityNodeInfosByText("领取")
                    if (coinInfo.size > 0) {
                        if (coinInfo[0].text == "领取" && coinInfo[0].isClickable) {
                            coinInfo[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            mClickGetCoin = true
                        }
                    }
                    val tmpInfo = rootInActiveWindow.findAccessibilityNodeInfosByText("我知道了")
                    if (tmpInfo.size > 0) {
                        tmpInfo[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        mClickGetCoin = false
                    }



                }
                "com.tanjinc.autotool" -> {
                    Log.d(TAG, "onAccessibilityEvent my " + event.text)
                    Log.d(TAG, "onAccessibilityEvent my " + event.packageName)
                    val nodeArray = rootInActiveWindow.findAccessibilityNodeInfosByViewId("$mTargetPackageName:id/testBtn")
                    if (nodeArray.size > 0) {
                        nodeArray[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                }
            }
        }

    }

    private fun recycle(nodeInfo:AccessibilityNodeInfo) {
        Log.d(TAG, ""+nodeInfo.childCount)
        val nodeArray = rootInActiveWindow.findAccessibilityNodeInfosByViewId("$mTargetPackageName:id/testBtn")
        if (nodeArray.size > 0) {
            nodeArray[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    override fun findFocus(focus: Int): AccessibilityNodeInfo {
        return super.findFocus(focus)
    }


    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, intent?.action)
            when(intent?.action) {
                "StartWork"-> {
                    Log.d(TAG, rootInActiveWindow.className.toString())
                }
            }
        }

    }

}
