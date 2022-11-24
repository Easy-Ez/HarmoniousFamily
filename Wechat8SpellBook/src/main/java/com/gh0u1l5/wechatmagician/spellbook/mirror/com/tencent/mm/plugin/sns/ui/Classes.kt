package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val SnsActivity: Class<*> by wxLazy("SnsActivity") {
        getSnsActivityByRules()
    }

    val SnsTimeLineUI: Class<*> by wxLazy("SnsTimeLineUI") {
        getSnsTimeLineUIByRules()
    }

    val SnsUploadUI: Class<*> by wxLazy("SnsUploadUI") {
        val classes = findClassesFromPackage(
            wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui"
        ).filterByField("$wxPackageName.plugin.sns.ui.LocationWidget")

        val clazzName = if (WechatGlobal.wxVersion!! >= Versions.v8_0_30) {
            "$wxPackageName.plugin.sns.ui.widget.AbsSnsUploadSayFooter"
        } else {
            "$wxPackageName.plugin.sns.ui.SnsUploadSayFooter"
        }
        classes.filterByField(clazzName).firstOrNull()
    }

    val SnsEditTextInterface: Class<*> by wxLazy("SnsEditTextInterface") {
        findClassIfExists(
            "com.tencent.mm.ui.widget.MMEditText", wxLoader!!
        )?.interfaces?.firstOrNull { `interface`-> `interface`.name.contains("com.tencent.mm") }
    }

    val SnsUserUI: Class<*> by wxLazy("SnsUserUI") {
        findClassIfExists("$wxPackageName.plugin.sns.ui.SnsUserUI", wxLoader!!)
    }


    private fun getSnsActivityByRules(): Class<*>? {
        return when {
            WechatGlobal.wxVersion!! >= Versions.v8_0_9 -> {
                findClassesFromPackage(
                    wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui"
                ).filterByField("$wxPackageName.ui.base.MMOverScrollView").firstOrNull()
            }
            else -> {
                findClassesFromPackage(
                    wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui"
                ).filterByField("$wxPackageName.ui.base.MMPullDownView").firstOrNull()
            }
        }
    }

    private fun getSnsTimeLineUIByRules(): Class<*>? {
        return when {
            WechatGlobal.wxVersion!! >= Versions.v8_0_6 -> {
                findClassesFromPackage(
                    wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui"
                ).filterByField("androidx.appcompat.app.ActionBar").firstOrNull()
            }
            else -> {
                findClassesFromPackage(
                    wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui"
                ).filterByField("android.support.v7.app.ActionBar").firstOrNull()
            }
        }
    }
}