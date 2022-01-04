package com.gh0u1l5.wechatmagician.spellbook.mirror.androidx.recyclerview.widget

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil

object Classes {
    private val Observable: Class<*> by WechatGlobal.wxLazy("Observable", Versions.v8_0_16) {
        ReflectionUtil.findClassIfExists(
            "android.database.Observable",
            WechatGlobal.wxLoader!!,
        )
    }
    val AdapterDataObservable: Class<*> by WechatGlobal.wxLazy(
        "RecyclerView.AdapterDataObservable",
        Versions.v8_0_16
    ) {
        ReflectionUtil.findClassesFromPackage(
            WechatGlobal.wxLoader!!,
            WechatGlobal.wxClasses!!,
            "androidx.recyclerview.widget"
        )
            .filterBySuper(Observable)
            .firstOrNull()
    }


}