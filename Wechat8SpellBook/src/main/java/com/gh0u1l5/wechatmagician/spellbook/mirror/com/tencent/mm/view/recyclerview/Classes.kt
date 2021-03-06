package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil

object Classes {
    val WxRecyclerAdapter: Class<*> by WechatGlobal.wxLazy("WxRecyclerAdapter", Versions.v8_0_11) {
        ReflectionUtil.findClassIfExists(
            "${WechatGlobal.wxPackageName}.view.recyclerview.WxRecyclerAdapter",
            WechatGlobal.wxLoader!!
        )
    }

    val SimpleViewHolder: Class<*> by WechatGlobal.wxLazy("SimpleViewHolder", Versions.v8_0_11) {
        ReflectionUtil.findClassesFromPackage(
            WechatGlobal.wxLoader!!,
            WechatGlobal.wxClasses!!,
            "${WechatGlobal.wxPackageName}.view.recyclerview"
        )
            .filterByMethod(
                WechatGlobal.wxLoader!!.loadClass("androidx.recyclerview.widget.RecyclerView"),
                "getRecyclerView"
            )
            .filterIsNotAbsClass()
            .firstOrNull()
    }

    /**
     * 用于寻找 WxRecyclerAdapter#onBindViewHolder 方法,
     */
    val FixedViewInfo: Class<*> by WechatGlobal.wxLazy(
        "RecyclerViewAdapterEx\$FixedViewInfo",
        Versions.v8_0_11
    ) {
        ReflectionUtil.findClassesFromPackage(
            WechatGlobal.wxLoader!!,
            WechatGlobal.wxClasses!!,
            "${WechatGlobal.wxPackageName}.view.recyclerview"
        )
            .filterIsAbsAndStaticClass()
            .filterByImplement(ConvertData)
            .firstOrNull()
    }

    val ConvertData: Class<*> by WechatGlobal.wxLazy("ConvertData", Versions.v8_0_11) {
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