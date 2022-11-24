package com.gh0u1l5.wechatmagician.spellbook.mirror.androidx.recyclerview.widget

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.mirror.androidx.recyclerview.widget.Classes.AdapterDataObservable
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes.WxRecyclerAdapter
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import java.lang.reflect.Field

object Fields {
    val Adapter_Observable: Field by WechatGlobal.wxLazy("Adapter_Observable", Versions.v8_0_11) {
        val clazz =
            if (WechatGlobal.wxVersion!! >= Versions.v8_0_30) WxRecyclerAdapter.superclass.superclass.superclass
            else WxRecyclerAdapter.superclass.superclass
        ReflectionUtil.findDeclaredFieldsWithType(
                clazz, AdapterDataObservable
            ).firstOrNull()?.apply { isAccessible = true }
    }
}