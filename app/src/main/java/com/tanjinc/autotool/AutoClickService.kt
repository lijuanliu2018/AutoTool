package com.tanjinc.autotool

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.lang.Exception
import java.util.*

class AutoClickService : AccessibilityService() {
    val TAG = "AutoClickService"
    val mQutoutiaoPackage = "com.jifen.qukan" //趣头条包名
    val mTargetPackageName = "com.tanjinc.autotool"
    private lateinit var mBrocardReceiver:MyBroadcastReceiver
    private var mClickGetCoin:Boolean = false
    private var mAdCloseBtnId = "com.jifen.qukan:id/pb"
    private val mPacketInstaller = "com.samsung.android.packageinstaller"

    private var mIsScrollIng = false

    val MSG_REFRESH_VIDEO = 100
    val MSG_RETURN_QU = 101
    val MSG_KILL = 102

    private var mIsShowResent = false
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what) {
                MSG_REFRESH_VIDEO -> {
                    clickByText("刷新")
                    Toast.makeText(MyApplication.getApplication(), "刷新", Toast.LENGTH_SHORT).show()
                    if (SharePreferenceUtil.getBoolean(Constants.VIDEO_TASK)) {
                        removeMessages(MSG_REFRESH_VIDEO)
                        sendEmptyMessageDelayed(MSG_REFRESH_VIDEO, 20 * 1000)
                    }
                }
                MSG_RETURN_QU -> {

                }
                MSG_KILL -> {
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mBrocardReceiver = MyBroadcastReceiver()
        registerReceiver(mBrocardReceiver, IntentFilter("StartWork"))


    }
    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        //设置关心的事件类型
        var info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100;//两个相同事件的超时时间间隔
        serviceInfo = info;
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        PrintUtils.printEvent(event)
        var rootInActiveWindow = rootInActiveWindow
        if (event != null && rootInActiveWindow != null) {
            Log.d(TAG, "event type = " + event.eventType)
            when(event.eventType) {
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    var data = event.parcelableData
                    if (data is Notification) {
                        var notification: Notification = data
                        Log.d(TAG, "notification= "+notification.tickerText)
                    }
                }
            }
            when(event.packageName) {
                mQutoutiaoPackage -> {
                    Log.d(TAG, "onAccessibilityEvent " + event.text)
                    Log.d(TAG, "onAccessibilityEvent " + event.packageName)
                    timeReward()
                    closeAdDialog()

                    //读取通知栏
                    if (event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                        if (event.parcelableData is Notification) {
                            val notification:Notification = event.parcelableData as Notification
                            val content = notification.tickerText.toString()
                            Log.d(TAG, "notification $content")
                        }
                    }

                    if (SharePreferenceUtil.getBoolean(Constants.VIDEO_TASK)) {
                        if (clickByText("小视频")) {
                            mHandler.sendEmptyMessageDelayed(100, 20 * 1000)
                        }
                    }

                    if (SharePreferenceUtil.getBoolean(Constants.SHIWAN_TASK)) {
                        recommendAppTask()

                        clickByRule()
                        if (findByText(rootInActiveWindow, "当前任务已抢光","xxxx") != null) {
                            SharePreferenceUtil.putBoolean(Constants.SHIWAN_TASK, false)
                        }
                        clickByText("进行中...")

                    }


                    clickByText("立即试玩")
                    installTask()
                    if (!clickByText("领取奖励")) {
                        findByText(rootInActiveWindow, "打开", "null", true)?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }

                    if (SharePreferenceUtil.getBoolean(Constants.QIANDAO_TASK)) {
                        recommendAppTask()
                        clickByText("我的福利")

                        var nodeInfo = findByText(rootInActiveWindow, "+120","已领")
                        var isClicked:Boolean ?= false
                        if (nodeInfo != null ) {
                            isClicked = when {
                                nodeInfo.isClickable -> nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                nodeInfo.parent.isClickable -> nodeInfo.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                else -> nodeInfo.parent?.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            }
                        }
                        if (isClicked != null && isClicked) {
                            Log.d(TAG, "task enter")
                            launch {
                                delay(35 * 1000) //延迟35秒
//                                mHandler.sendEmptyMessageDelayed(MSG_KILL, 35 * 1000);
                                mIsShowResent = true

                                performGlobalAction(GLOBAL_ACTION_RECENTS)
                            }
                        } else {
                            var scrollView = findByViewName(rootInActiveWindow, "android.widget.ListView")
                            scrollView?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                        }
                    }
                    val readPage = true
                    if (readPage) {
                        val webNodeInfo = findByViewName(rootInActiveWindow, "android.webkit.WebView")
                        if (webNodeInfo!= null && webNodeInfo.isScrollable && !mIsScrollIng) {
                            launch {

                                mIsScrollIng = true
                                var i = 0
                                while ( i < 5) {
                                    webNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                                    delay(Random().nextInt(10) * 1000)
                                    i++
                                }

                                performGlobalAction(GLOBAL_ACTION_BACK)
                                mIsScrollIng = false
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
                "com.android.systemui" -> {
                    val nodeInfo = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.android.systemui:id/img")
                    if (mIsShowResent) {
                        clickByText("趣头条")
                        mIsShowResent = false
                    }
                }

            }
        }

    }

    private fun installTask() {
        clickByText("完成")
        clickByText("安装")
        clickByText("确认")
        clickByText("继续")
        clickByText("下一步")
        clickById("com.android.packageinstaller:id/decide_to_continue")
        clickByText("继续安装")
        clickByText("打开阅读")
    }
    //领取时段奖励
    private fun timeReward() {
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
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return false
    }

    private fun clickById(id:String?) {
        if (rootInActiveWindow == null) {
            return
        }
        try {

            val targetNodeInfo = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id)
            if (targetNodeInfo.size > 0) {
                if (targetNodeInfo[0].isClickable) {
                    targetNodeInfo[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                } else {
                    targetNodeInfo[0].parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            }
        } catch (e:Exception) {

        }
    }

    private fun clickByRule() :Boolean{
        var targetNode = findByText(rootInActiveWindow, "人试玩", "")
        if (targetNode != null) {
            targetNode.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        return false
    }


    private fun findByViewName(rootNodeInfo: AccessibilityNodeInfo?, text: String) : AccessibilityNodeInfo?{
        if (rootNodeInfo == null) {
            return null
        }
        var targetNodeInf:AccessibilityNodeInfo ?= null
        try {

            for (i in 0 until rootNodeInfo.childCount) {
                var nodeI = rootNodeInfo.getChild(i)
                if (nodeI != null) {
                    Log.d(TAG, " findByViewName className= " + nodeI.className)
                    if (nodeI.className != null && nodeI.className.contains(text) && nodeI.isScrollable) {
                        targetNodeInf = nodeI
                        break
                    } else {
                        targetNodeInf = findByViewName(nodeI, text)
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

    //遍历查找
    private fun findByText(rootNodeInfo: AccessibilityNodeInfo?, text: String, excText:String = "null", end:Boolean = false) : AccessibilityNodeInfo?{
        if (rootNodeInfo == null) {
            return null
        }
        var targetNodeInf:AccessibilityNodeInfo ?= null
        try {

            for (i in 0 until rootNodeInfo.childCount) {
                var nodeI = rootNodeInfo.getChild(if(!end) i else rootNodeInfo.childCount - 1- i)
                if (nodeI != null) {
                    Log.d(TAG, " findByText text= " + nodeI.text)
                    if (nodeI.text != null && nodeI.text.contains(text) && !nodeI.text.contains(excText)) {
                        targetNodeInf = nodeI
                        break
                    } else {
                        targetNodeInf = findByText(nodeI, text, excText)
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
