package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.util.Log
import android.widget.BaseAdapter
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.Predicate
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.data.Record
import com.gh0u1l5.wechatmagician.spellbook.data.Section
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.Classes.MMBaseAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.Methods.MMBaseAdapter_getItemInternal
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.AddressAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.MMSearchContactAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.RecentConversationAdapter
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.conversation.Classes.ConversationWithCacheAdapter
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClassIfExists
import java.util.concurrent.ConcurrentHashMap

object ListViewHider : HookerProvider {


    val records: MutableMap<BaseAdapter, Record> = ConcurrentHashMap()

    fun register(adapter: BaseAdapter, predicateName: String, predicateBody: Predicate) {
        val record = records[adapter]
        if (record == null) {
            records[adapter] = Record(emptyList(), mapOf(predicateName to predicateBody))
            return
        }
        synchronized(record) {
            record.predicates += (predicateName to predicateBody)
        }
    }

    private fun updateAdapterSections(param: XC_MethodHook.MethodHookParam) {
        val adapter = param.thisObject as BaseAdapter
        val record = records[adapter] ?: return
        synchronized(record) {
            record.sections = emptyList()
            val initial = listOf(Section(0, adapter.count, 0))
            val predicates = record.predicates.values
            record.sections = (0 until adapter.count).filter { index ->
                // Hide the items satisfying any one of the predicates
                val item = adapter.getItem(index)
                predicates.forEach { predicate ->
                    if (predicate(item)) {
                        return@filter true
                    }
                }
                return@filter false
            }.fold(initial) { sections, index ->
                sections.dropLast(1) + sections.last().split(index)
            }
        }
    }

    override fun provideStaticHookers(): List<Hooker> {
        return listOf(
            RecentConversationAdapterHooker,
            MMSelectContactAdapterHooker,
            MMBaseAdapterHooker,
            BaseAdapterHooker
        )
    }

    private val RecentConversationAdapterHooker = Hooker {
        // Hook getItem() of base adapters
//        findAndHookMethod(
//            RecentConversationAdapter,
//            "vD",
//            C.Int,
//            object : XC_MethodHook() {
//                override fun beforeHookedMethod(param: MethodHookParam) {
//                    val adapter = param.thisObject as BaseAdapter
//                    val index = param.args[0] as Int
//                    Log.d("yaocai-adapter", "getItem, index:${index}")
//                    val record = records[adapter] ?: return
//                    synchronized(record) {
//                        record.sections.forEach { section ->
//                            if (index in section) {
//                                param.args[0] = section.base + (index - section.start)
//                                return
//                            }
//                        }
//                    }
//                }
//            })
//        // Hook getCount() of base adapters
//        findAndHookMethod(RecentConversationAdapter, "getCount", object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam) {
//                val adapter = param.thisObject as BaseAdapter
//                val record = records[adapter] ?: return
//                synchronized(record) {
//                    if (record.sections.isNotEmpty()) {
//                        Log.d(
//                            "yaocai-adapter",
//                            "getCount:${record.sections.sumOf { it.size() }}"
//                        )
//                        param.result = record.sections.sumOf { it.size() }
//                    }
//                }
//            }
//        })
    }
    private val MMSelectContactAdapterHooker = Hooker {
        // Hook getItem() of base adapters
//        findAndHookMethod(
//            MMSearchContactAdapter,
//            "vD",
//            C.Int,
//            object : XC_MethodHook() {
//                override fun beforeHookedMethod(param: MethodHookParam) {
//                    val adapter = param.thisObject as BaseAdapter
//                    val index = param.args[0] as Int
//                    Log.d("yaocai-adapter", "getItem, index:${index}")
//                    val record = records[adapter] ?: return
//                    synchronized(record) {
//                        record.sections.forEach { section ->
//                            if (index in section) {
//                                param.args[0] = section.base + (index - section.start)
//                                return
//                            }
//                        }
//                    }
//                }
//            })
//
//        // Hook getCount() of base adapters
//        findAndHookMethod(MMSearchContactAdapter, "getCount", object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam) {
//                val adapter = param.thisObject as BaseAdapter
//                val record = records[adapter] ?: return
//                synchronized(record) {
//                    if (record.sections.isNotEmpty()) {
//                        Log.d(
//                            "yaocai-adapter",
//                            "getCount:${record.sections.sumOf { it.size() }}"
//                        )
//                        param.result = record.sections.sumOf { it.size() }
//                    }
//                }
//            }
//        })


    }

    private val MMBaseAdapterHooker = Hooker {
        // Hook getItem() of base adapters
        findAndHookMethod(
            MMBaseAdapter,
            MMBaseAdapter_getItemInternal,
            C.Int,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val adapter = param.thisObject as BaseAdapter
                    val originIndex = param.args[0] as Int
                    val record = records[adapter] ?: return
                    synchronized(record) {
                        record.sections.forEach { section ->
                            if (originIndex in section) {
                                val newIndex = section.base + (originIndex - section.start)
                                Log.d(
                                    "yaocai-adapter",
                                    "getItem originIndex:${originIndex};newIndex:${newIndex}"
                                )
                                param.args[0] = newIndex
                                return
                            }
                        }
                    }
                }
            })

        // Hook getCount() of base adapters
        findAndHookMethod(MMBaseAdapter, "getCount", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val adapter = param.thisObject as BaseAdapter
                val record = records[adapter] ?: return
                synchronized(record) {
                    if (record.sections.isNotEmpty()) {
                        val newCount = record.sections.sumOf { it.size() }
                        Log.d(
                            "yaocai-adapter",
                            "getItem newCount:${newCount}"
                        )
                        param.result = newCount
                    }
                }
            }
        })

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_BASE_ADAPTER)
    }

    private val BaseAdapterHooker = Hooker {
        // Hook notifyDataSetChanged() of base adapters
        findAndHookMethod(C.BaseAdapter, "notifyDataSetChanged", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                Log.d("yaocai-adapter", "notifyDataSetChanged:${param.thisObject}")
                Log.d(
                    "yaocai-adapter",
                    "notifyDataSetChanged isSame:${param.thisObject::class.java == ConversationWithCacheAdapter}"
                )
                when (param.thisObject::class.java) {
                    AddressAdapter -> {
                        updateAdapterSections(param)
                    }
                    ConversationWithCacheAdapter -> {
                        updateAdapterSections(param)
                    }
                    MMSearchContactAdapter -> {
                        updateAdapterSections(param)
                    }
                }
            }
        })

        //todo error
//        findAndHookMethod(
//            findClassIfExists("android.widget.ListView", WechatGlobal.wxLoader!!),
//            "setAdapter",
//            object : XC_MethodHook() {
//                override fun beforeHookedMethod(param: MethodHookParam) {
//                    val adapter = param.args[0]
//                    Log.d(
//                        "yaocai-adapter",
//                        "setAdapter:${adapter},isRecentAdapter:${adapter::class.java == RecentConversationAdapter}"
//                    )
//                    when (adapter::class.java) {
//                        RecentConversationAdapter -> {
//                            updateAdapterSections(param)
//                        }
//                    }
//                }
//            })
    }
}
