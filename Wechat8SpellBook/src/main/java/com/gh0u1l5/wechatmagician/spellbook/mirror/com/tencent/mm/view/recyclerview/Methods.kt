package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview

import android.view.View
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes.ConvertData
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes.WxRecyclerAdapter
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import java.lang.reflect.Method

object Methods {

    /**
     *  WxRecyclerAdapter 的 header 和 footer 的  bindViewHolder   由 RecyclerViewAdapterEx 类实现, 子类可以重写该方法
     *  RecyclerViewAdapterEx 实现了的[androidx.recyclerview.widget.RecyclerView] 的 onBindViewHolder 方法
     *  分别分发 header   footer 以及 Normal Item 的 bindViewHolder
     *  子类即 WxRecyclerAdapter 重写了 RecyclerViewAdapterEx 关于  Normal Item 的 bindViewHolder 的两个方法 (一个有 payloads)
     *  这两个方法最终会调用签名为 (View,ConvertData,Int) : Void 的方法, 设置点击相关事件, 因此该方法作为特征用于处理 item 的隐藏和展示
     *
     */
    val WxRecyclerAdapter_getOnItemConvertClickListener: Method by WechatGlobal.wxLazy(
        "WxRecyclerAdapter_onBindViewHolder",
        Versions.v8_0_16
    ) {
        ReflectionUtil.findMethodsByExactParameters(
            WxRecyclerAdapter,
            null,
            View::class.java,
            ConvertData,
            C.Int,
        ).firstOrNull()
    }


}