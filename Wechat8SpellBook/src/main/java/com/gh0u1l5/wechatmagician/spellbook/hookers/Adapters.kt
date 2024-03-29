package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.data.InnerAdapter
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IAdapterHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.AddressAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.MMSearchContactAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.RecentConversationAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Methods.AddressUI_createMvvmRecyclerAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.conversation.Classes.ConversationWithCacheAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes.WxRecyclerAdapter
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedBridge.hookMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findMethodExact

object Adapters : EventCenter() {

    override val interfaces: List<Class<*>>
        get() = listOf(IAdapterHook::class.java)

    override fun provideEventHooker(event: String) = when (event) {
        "onAddressAdapterCreated" ->
            onAddressAdapterCreateHooker
        "onMMSearchContactAdapterCreated" ->
            onMMSearchContactAdapterCreatedHooker
        "onRecentConversationAdapterCreated" ->
            onRecentConversationAdapterCreatedHooker
        "onConversationAdapterCreated" ->
            onConversationWithCacheAdapterCreateHooker
        "onHeaderViewListAdapterGettingView", "onHeaderViewListAdapterGotView" ->
            onHeaderViewListAdapterGetViewHooker
        else ->
            throw IllegalArgumentException("Unknown event: $event")
    }

    private val onAddressAdapterCreateHooker = Hooker {
        hookAllConstructors(AddressAdapter, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val adapter = param.thisObject as? BaseAdapter
                if (adapter == null) {
                    Log.d(
                        "Xposed",
                        "Expect address adapter to be BaseAdapter, get ${param.thisObject::class.java}"
                    )
                    return
                }
                notify("onAddressAdapterCreated") { plugin ->
                    (plugin as IAdapterHook).onAddressAdapterCreated(adapter)
                }
            }
        })
        hookMethod(AddressUI_createMvvmRecyclerAdapter, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val adapter = param.result
                // 这里加强下判断, adapter 是否为 WxRecyclerAdapter 的子类, 此时 adapter 人应为MvvmRecyclerAdapter
                if (adapter == null || !WxRecyclerAdapter.isAssignableFrom(adapter::class.java)) {
                    Log.d(
                        "InnerAdapter",
                        "Expect address adapter to be RecyclerView.Adapter, get ${param.result::class.java}"
                    )
                    return
                }

                notify("onAddressAdapterCreated") { plugin ->
                    Log.d("InnerAdapter", "AddressItemConvert ,thisObject:${adapter}")
                    (plugin as IAdapterHook).onAddressAdapterCreated(InnerAdapter(adapter))
                }
            }
        })

    }

    private val onConversationWithCacheAdapterCreateHooker = Hooker {
        hookAllConstructors(ConversationWithCacheAdapter, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val adapter = param.thisObject as? BaseAdapter
                if (adapter == null) {
                    Log.d(
                        "Xposed",
                        "Expect conversation adapter to be BaseAdapter, get ${param.thisObject::class.java}"
                    )
                    return
                }
                notify("onConversationAdapterCreated") { plugin ->
                    (plugin as IAdapterHook).onConversationAdapterCreated(adapter)
                }
            }
        })
    }

    private val onMMSearchContactAdapterCreatedHooker = Hooker {
        hookAllConstructors(MMSearchContactAdapter, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val adapter = param.thisObject as? BaseAdapter
                if (adapter == null) {
                    Log.d(
                        "Xposed",
                        "Expect conversation adapter to be BaseAdapter, get ${param.thisObject::class.java}"
                    )
                    return
                }
                notify("onMMSearchContactAdapterCreatedHooker") { plugin ->
                    (plugin as IAdapterHook).onMMSearchContactAdapterCreated(adapter)
                }
            }
        })
    }

    private val onRecentConversationAdapterCreatedHooker = Hooker {
        hookAllConstructors(RecentConversationAdapter, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val adapter = param.thisObject as? BaseAdapter
                if (adapter == null) {
                    Log.d(
                        "Xposed",
                        "Expect conversation adapter to be BaseAdapter, get ${param.thisObject::class.java}"
                    )
                    return
                }
                notify("onRecentConversationAdapterCreated") { plugin ->
                    (plugin as IAdapterHook).onRecentConversationAdapterCreated(adapter)
                }
            }
        })
    }

    private val onHeaderViewListAdapterGetViewHooker = Hooker {
        findAndHookMethod(
            C.HeaderViewListAdapter, "getView",
            C.Int, C.View, C.ViewGroup, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val adapter = param.thisObject
                    val position = param.args[0] as Int
                    val convertView = param.args[1] as View?
                    val parent = param.args[2] as ViewGroup
                    notifyForOperations("onHeaderViewListAdapterGettingView", param) { plugin ->
                        (plugin as IAdapterHook).onHeaderViewListAdapterGettingView(
                            adapter,
                            position,
                            convertView,
                            parent
                        )
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    val adapter = param.thisObject
                    val position = param.args[0] as Int
                    val convertView = param.args[1] as View?
                    val parent = param.args[2] as ViewGroup
                    val result = param.result as View?
                    notifyForOperations("onHeaderViewListAdapterGotView", param) { plugin ->
                        (plugin as IAdapterHook).onHeaderViewListAdapterGotView(
                            adapter,
                            position,
                            convertView,
                            parent,
                            result
                        )
                    }
                }
            })
    }
}