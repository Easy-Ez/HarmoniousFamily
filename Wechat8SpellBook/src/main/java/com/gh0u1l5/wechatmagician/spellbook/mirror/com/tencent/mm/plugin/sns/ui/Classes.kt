package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Classes
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val SnsActivity: Class<*> by wxLazy("SnsActivity") {
        getSnsActivityByRules()
    }

    val SnsTimeLineUI: Class<*> by wxLazy("SnsTimeLineUI") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui")
            .filterByField("androidx.appcompat.app.ActionBar")
            .firstOrNull()
    }

    val SnsUploadUI: Class<*> by wxLazy("SnsUploadUI") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui")
            .filterByField("$wxPackageName.plugin.sns.ui.LocationWidget")
            .filterByField("$wxPackageName.plugin.sns.ui.SnsUploadSayFooter")
            .firstOrNull()
    }

    val SnsEditTextInterface: Class<*> by wxLazy("SnsEditTextInterface") {
        findClassIfExists(
            "com.tencent.mm.ui.widget.MMEditText",
            wxLoader!!
        )?.interfaces?.firstOrNull()
    }

    val SnsUserUI: Class<*> by wxLazy("SnsUserUI") {
        findClassIfExists("$wxPackageName.plugin.sns.ui.SnsUserUI", wxLoader!!)
    }


    private fun getSnsActivityByRules(): Class<*>? {
        return when {
            WechatGlobal.wxVersion!! >= Version("8.0.16") -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui")
                    .filterByField("$wxPackageName.ui.base.MMOverScrollView")
                    .firstOrNull()
            }
            else -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui")
                    .filterByField("$wxPackageName.ui.base.MMPullDownView")
                    .firstOrNull()
            }
        }
    }

}