package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.mvvmlist

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes.WxRecyclerAdapter
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil

object Classes {
    val MvvmRecyclerAdapter: Class<*> by WechatGlobal.wxLazy(
        "MvvmRecyclerAdapter",
        Versions.v8_0_11
    ) {
        ReflectionUtil.findClassesFromPackage(
            WechatGlobal.wxLoader!!,
            WechatGlobal.wxClasses!!,
            "${WechatGlobal.wxPackageName}.plugin.mvvmlist"
        )
            .filterBySuper(WxRecyclerAdapter)
            .firstOrNull()

    }

}