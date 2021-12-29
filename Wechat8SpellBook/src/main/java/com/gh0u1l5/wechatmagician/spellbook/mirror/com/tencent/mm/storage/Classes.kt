package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val MsgInfo: Class<*> by wxLazy("MsgInfo") {
        getMsgInfoByRules()
    }

    val MsgInfoStorage: Class<*> by wxLazy("MsgInfoStorage") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.storage")
            .filterByMethod(C.Long, MsgInfo, C.Boolean)
            .firstOrNull()
    }

    val ContactInfo: Class<*> by wxLazy("ContactInfo") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.storage")
            .filterByMethod(C.String, "getCityCode")
            .filterByMethod(C.String, "getCountryCode")
            .firstOrNull()
    }

    /**
     * 搜索 voip_content_voice , voip_content_video 字符串可以快速定位
     */
    private fun getMsgInfoByRules(): Class<*>? {
        return when {
            WechatGlobal.wxVersion!! >= Versions.v8_0_6 -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.storage")
                    .filterAnonymousClass()
//                    .filterByValueOfStaticField("voip_content_voice")
//                    .filterByValueOfStaticField("voip_content_video")
                    .filterByMethod(C.Boolean, "isOmittedFailResend")
                    .filterByMethod(null, "setStatus", C.Int)
                    .firstOrNull()
            }
            else -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.storage")
                    .filterByMethod(C.Boolean, "isSystem")
                    .firstOrNull()
            }
        }
    }
}