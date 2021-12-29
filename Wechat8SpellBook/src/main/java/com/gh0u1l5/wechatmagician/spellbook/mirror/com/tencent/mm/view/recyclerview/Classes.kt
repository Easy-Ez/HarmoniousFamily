package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil

object Classes {
    val WxRecyclerAdapter: Class<*> by WechatGlobal.wxLazy("WxRecyclerAdapter", Versions.v8_0_16) {
        ReflectionUtil.findClassIfExists(
            "${WechatGlobal.wxPackageName}.view.recyclerview.WxRecyclerAdapter",
            WechatGlobal.wxLoader!!
        )
    }

    val ConvertData: Class<*> by WechatGlobal.wxLazy("ConvertData", Versions.v8_0_16) {
        ReflectionUtil.findClassesFromPackage(
            WechatGlobal.wxLoader!!,
            WechatGlobal.wxClasses!!,
            "${WechatGlobal.wxPackageName}.view.recyclerview",
        )
            .filterIsInterface()
            .filterByMethod(C.Long)
            .filterByMethod(C.Int)
            .isOnlyOneOrContinue {
                it.filterByDeclaredMethodCount(2)
                    .firstOrNull()
            }
    }

}