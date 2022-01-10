package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxVersion
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.mvvmlist.Classes.MvvmRecyclerAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.AddressItemConvert
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.AddressUI
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.ContactLongClickListener
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.MMSelectContactAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.OnCreateContextMenuListener
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes.ConvertData
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findMethodsByExactParameters
import java.lang.reflect.Method

object Methods {
    val ContactLongClickListener_onItemLongClick: Method by wxLazy("ContactLongClickListener_onItemLongClick") {
        findOnItemLongClickMethod()
    }

    val ContactLongClickListener_onCreateContextMenu: Method by wxLazy("ContactLongClickListener_onCreateContextMenu") {
        findMethodsByExactParameters(
            OnCreateContextMenuListener,
            null,
            C.ContextMenu,
            C.View,
            C.ContextMenuInfo
        ).firstOrNull()
    }
    val MMSelectContactAdapter_getItemInternal: String by wxLazy("MMSelectContactAdapter_getItemInternal") {
        MMSelectContactAdapter.declaredMethods.filter {
            it.parameterTypes.size == 1 && it.parameterTypes[0] == C.Int
        }.firstOrNull {
            it.name != "getItem" && it.name != "getItemId"
        }?.name
    }


    /**
     * MvvmAddressUIFragment$Companion 内部会创建 MvvmRecyclerAdapter 用于展示通讯录列表
     */
    val AddressUI_createMvvmRecyclerAdapter: Method by wxLazy(
        "AddressUI_createMvvmRecyclerAdapter",
        Versions.v8_0_11
    ) {
        findMethodsByExactParameters(
            AddressUI,
            MvvmRecyclerAdapter,
        ).firstOrNull()
    }
    val AddressItemConvert_onBindViewHolder: Method by wxLazy(
        "AddressItemConvert_onBindViewHolder",
        Versions.v8_0_11
    ) {
        findMethodsByExactParameters(
            AddressItemConvert,
            null,
            Classes.SimpleViewHolder,
            ConvertData,
            C.Int,
            C.Int,
            C.Boolean,
            C.List
        ).firstOrNull()
    }


    private fun findOnItemLongClickMethod(): Method? {
        return when {
            wxVersion!! >= Versions.v8_0_11 -> {
                findMethodsByExactParameters(
                    ContactLongClickListener,
                    C.Boolean,
                    C.View,
                    ConvertData,
                    C.Int
                ).firstOrNull()
            }
            else -> {
                ReflectionUtil.findMethodExactIfExists(
                    ContactLongClickListener,
                    "onItemLongClick",
                    C.AdapterView, C.View, C.Int, C.Long,
                )?.apply { isAccessible = true }
            }
        }
    }
}