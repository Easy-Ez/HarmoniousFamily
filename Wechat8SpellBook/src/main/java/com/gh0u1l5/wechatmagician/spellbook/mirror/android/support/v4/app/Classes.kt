package com.gh0u1l5.wechatmagician.spellbook.mirror.android.support.v4.app

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val NotificationManagerCompat: Class<*> by wxLazy("NotificationManagerCompat") {
        getNotificationManagerCompatByRules()

    }

    private fun getNotificationManagerCompatByRules(): Class<*>? {
        return when {
            WechatGlobal.wxVersion!! >= Version("8.0.6") -> {
                // v4切换成 androidx 兼容包
                findClassesFromPackage(wxLoader!!, wxClasses!!, "androidx.core.app")
                    .filterByField("android.app.NotificationManager")
                    .firstOrNull()
            }
            else -> {
                // v4切换成 androidx 兼容包
                findClassesFromPackage(wxLoader!!, wxClasses!!, "android.support.v4.app")
                    .filterByField("android.app.NotificationManager")
                    .firstOrNull()

            }
        }
    }

}