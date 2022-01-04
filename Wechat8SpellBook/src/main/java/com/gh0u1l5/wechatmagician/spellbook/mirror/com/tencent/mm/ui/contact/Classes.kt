package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxVersion
import com.gh0u1l5.wechatmagician.spellbook.base.Versions.v8_0_11
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage.Classes.ContactInfo
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes.ConvertData
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes.SimpleViewHolder
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

/**
 * 从某个版本开始, 通讯录列表从 ListView 换成了 RecyclerView 实现
 * 实现类核心类由 com.tencent.mm.ui.contact.AddressUI$AddressUIFragment 替换为 com.tencent.mm.ui.contact.address.MvvmAddressUIFragment
 */
object Classes {
    val AddressUI: Class<*> by wxLazy("AddressUI") {
        findAddressUI()
    }

    val AddressAdapter: Class<*> by wxLazy("AddressAdapter") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.contact")
            .filterByMethod(null, "pause")
            .firstOrNull()
    }

    val ContactLongClickListener: Class<*> by wxLazy("ContactLongClickListener") {
        findContactLongClickListener()
    }
    val OnCreateContextMenuListener: Class<*> by wxLazy("OnCreateContextMenuListener") {
        findOnCreateContextMenuListener()
    }

    /**
     * 新版通讯录 list item bean
     */
    val AddressLiveListItem: Class<*> by wxLazy("AddressLiveListItem", v8_0_11) {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.contact.address")
            .filterByFieldType(ContactInfo)
            .firstOrNull()
    }

    /**
     * 通讯录 ItemType 转换器
     * a(j jVar, d dVar, int i, int i2, boolean z, List list)
     */
    val AddressItemConvert: Class<*> by wxLazy("AddressItemConvert", v8_0_11) {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.contact.address")
            .filterByMethod(
                null,
                SimpleViewHolder,
                ConvertData,
                C.Int,
                C.Int,
                C.Boolean,
                C.List
            )
            .firstOrNull()
    }

    val SelectContactUI: Class<*> by wxLazy("SelectContactUI") {
        findClassIfExists("$wxPackageName.ui.contact.SelectContactUI", wxLoader!!)
    }

    /**
     * 通讯录对应的 fragment
     */
    private fun findAddressUI(): Class<*>? {
        return when {
            wxVersion!! >= v8_0_11 -> {
                findClassIfExists(
                    "$wxPackageName.ui.contact.address.MvvmAddressUIFragment",
                    wxLoader!!
                )
            }
            else -> {
                findClassIfExists(
                    "$wxPackageName.ui.contact.AddressUI\$AddressUIFragment",
                    wxLoader!!
                )
            }
        }
    }

    /**
     * 寻找通讯录 listView or recyclerView 的长按事件对应类
     */
    private fun findContactLongClickListener(): Class<*>? {
        return when {
            wxVersion!! >= v8_0_11 -> {
                findClassesFromPackage(
                    wxLoader!!,
                    wxClasses!!,
                    "$wxPackageName.ui.contact.address"
                )
                    .filterIsAnonymousClass()
                    .filterByMethod(C.Boolean, C.View, ConvertData, C.Int)
                    .firstOrNull()
            }
            else -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.contact")
                    .filterByEnclosingClass(AddressUI)
                    .filterByMethod(
                        C.Boolean,
                        "onItemLongClick",
                        C.AdapterView,
                        C.View,
                        C.Int,
                        C.Long
                    )
                    .firstOrNull()
            }
        }
    }

    /**
     * 新版本会创建内部类实现 View.OnCreateContextMenuListener 接口
     * 老版本 Fragment 直接实现该接口
     */
    private fun findOnCreateContextMenuListener(): Class<*>? {
        return when {
            wxVersion!! >= v8_0_11 -> {
                findClassesFromPackage(
                    wxLoader!!,
                    wxClasses!!,
                    "$wxPackageName.ui.contact.address"
                )
                    .filterByEnclosingClass(AddressUI)
                    .filterByMethod(
                        null,
                        C.ContextMenu,
                        C.View,
                        C.ContextMenuInfo
                    )
                    .firstOrNull()
            }
            else -> {
                AddressUI
            }
        }
    }


}