package com.tanjinc.autotool

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.lang.Exception

class AutoClickService : AccessibilityService() {
    val TAG = "AutoClickService"
    val mQutoutiaoPackage = "com.jifen.qukan" //趣头条包名
    val mTargetPackageName = "com.tanjinc.autotool"
    private lateinit var mBrocardReceiver:MyBroadcastReceiver
    private var mClickGetCoin:Boolean = false
    private var mAdCloseBtnId = "com.jifen.qukan:id/pb"
    private val mPacketInstaller = "com.samsung.android.packageinstaller"

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

                    shiduanJianli()
                    closeAdDialog()
                    recommendAppTask()
//                    if (findByText(rootInActiveWindow, "当前任务已抢光") != null) {
//                        clickByRule()
//                    } else {
//                        clickByText("我的福利")
//                    }
                    clickByRule()

                    clickByText("立即试玩")
                    clickByText("安装")
                    clickByText("下一步")
                    if (!clickByText("领取奖励")) {
                        clickByText("领取奖励")
                        clickByText("打开使用")
                        clickByText("打开阅读")
                        clickByText("打开试玩")
                    }

                    if (SharePreferenceUtil.getSharePreference(Constants.QIANDAO_TASK) == true) {
                        clickByText("我的")
                        clickByText("推荐应用")
                        clickByText("我的福利")
                        var nodeInfo = findByText(rootInActiveWindow, "+120")
                        if (nodeInfo != null) {
                            when {
                                nodeInfo.isClickable -> nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                nodeInfo.parent.isClickable -> nodeInfo.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                else -> nodeInfo.parent.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            }

                        }
                    }

                    rootInActiveWindow?.recycle()
                }
                "com.tanjinc.autotool" -> {
                    val nodeArray = rootInActiveWindow.findAccessibilityNodeInfosByViewId("$mTargetPackageName:id/testBtn")
                    if (nodeArray.size > 0) {
                        nodeArray[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                }
                "com.android.packageinstaller" ->  installTask()
                mPacketInstaller -> installTask()

            }
        }

    }

    private fun installTask() {
        clickByText("安装")
        clickByText("确认")
        clickByText("完成")
    }
    //领取时段奖励
    private fun shiduanJianli() {
        clickByText("领取")
        clickByText("我知道了")
    }

    private fun closeAdDialog() {
        clickById(mAdCloseBtnId)
    }

    private fun recommendAppTask() {
        clickByText("我的")
        clickByText("推荐应用")

    }

    private fun clickByText(text:String) :Boolean{
        if (rootInActiveWindow == null) {
            return false
        }
        val targetNodeInfo = rootInActiveWindow?.findAccessibilityNodeInfosByText(text)
        if(targetNodeInfo != null && targetNodeInfo.size> 0 ) {
            for (i in 0 until targetNodeInfo.size) {
                if (targetNodeInfo[i]?.text == text) {
                    if (targetNodeInfo[i].isClickable) {
                        Log.d(TAG, "clickByText click $text success")
                        return targetNodeInfo[i].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    } else {
                        Log.d(TAG, "clickByText click $text parent success")
                        return targetNodeInfo[i]?.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)!!
                    }
                }
            }
        }
        return false
    }

    private fun clickById(id:String) {
        if (rootInActiveWindow == null) {
            return
        }
        val targetNodeInfo = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id)
        if (targetNodeInfo.size > 0) {
            if (targetNodeInfo[0].isClickable) {
                targetNodeInfo[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            } else {
                targetNodeInfo[0].parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    private fun clickByRule() :Boolean{
        var targetNode = findByText(rootInActiveWindow, "人试玩")
        if (targetNode != null) {
            targetNode.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        return false
    }



    //遍历查找
    private fun findByText(rootNodeInfo: AccessibilityNodeInfo?, text: String) : AccessibilityNodeInfo?{
        if (rootNodeInfo == null) {
            return null
        }
        var targetNodeInf:AccessibilityNodeInfo ?= null
        try {

            for (i in 0 until rootNodeInfo.childCount) {
                var nodeI = rootNodeInfo.getChild(i)
                if (nodeI != null) {
                    Log.d(TAG, " findByText text= " + nodeI.text)
                    if (nodeI.text != null && nodeI.text.contains(text)) {
                        targetNodeInf = nodeI
                        break
                    } else {
                        targetNodeInf = findByText(nodeI, text)
                    }
                }
                if (targetNodeInf != null) {
                    return targetNodeInf
                }
            }

        } catch (exception:Exception) {

        }
        return targetNodeInf
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
