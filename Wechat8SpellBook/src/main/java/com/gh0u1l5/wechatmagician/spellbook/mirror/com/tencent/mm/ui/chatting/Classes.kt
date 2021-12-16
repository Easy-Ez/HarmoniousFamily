package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.chatting

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Classes
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.Classes.MMFragmentActivity
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {

    val ChattingUI: Class<*> by wxLazy("ChattingUI") {
        getChattingUIByRules()
    }

    /**
     * 从某个版本开始, 继承 MMSecDataFragmentActivity 间接继承 MMFragmentActivity
     * 这里增加 filterBySuper depth 参数, 最多找几层
     */
    private fun getChattingUIByRules(): Class<*>? {
        return when {
            WechatGlobal.wxVersion!! >= Version("8.0.16") -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.chatting")
                    .filterAnonymousClass()
                    .filterBySuper(MMFragmentActivity, 2)
                    .filterByMethod(
                        null,
                        "onRequestPermissionsResult",
                        C.Int,
                        C.StringArray,
                        C.IntArray
                    )
                    .firstOrNull()
            }
            WechatGlobal.wxVersion!! >= Version("8.0.1") -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.chatting")
                    .filterAnonymousClass()
                    .filterBySuper(MMFragmentActivity, 1)
                    .filterByMethod(
                        null,
                        "onRequestPermissionsResult",
                        C.Int,
                        C.StringArray,
                        C.IntArray
                    )
                    .firstOrNull()
            }
            else -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.chatting")
                    .filterBySuper(MMFragmentActivity)
                    .filterByMethod(
                        null,
                        "onRequestPermissionsResult",
                        C.Int,
                        C.StringArray,
                        C.IntArray
                    )
                    .firstOrNull()
            }
        }
    }
}