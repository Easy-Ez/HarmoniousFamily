package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.Predicate
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.data.Record
import com.gh0u1l5.wechatmagician.spellbook.data.Section
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.Classes
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.Methods
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.util.concurrent.ConcurrentHashMap

object RecyclerViewHider : HookerProvider {

    private val records: MutableMap<RecyclerView.Adapter<*>, Record> = ConcurrentHashMap()

    fun register(
        adapter: RecyclerView.Adapter<*>,
        predicateName: String,
        predicateBody: Predicate
    ) {
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
        val adapter = param.thisObject as RecyclerView.Adapter<*>
        val record = records[adapter] ?: return
        synchronized(record) {
//            record.sections = emptyList()
//            val initial = listOf(Section(0, adapter.itemCount, 0))
//            val predicates = record.predicates.values
//            record.sections = (0 until adapter.itemCount).filter { index ->
//                // Hide the items satisfying any one of the predicates
//                val item = adapter.getItem(index)
//                predicates.forEach { predicate ->
//                    if (predicate(item)) {
//                        return@filter true
//                    }
//                }
//                return@filter false
//            }.fold(initial) { sections, index ->
//                sections.dropLast(1) + sections.last().split(index)
//            }
        }
    }


    override fun provideStaticHookers(): List<Hooker> {
        return listOf(MMBaseAdapterHooker)
    }

    private val MMBaseAdapterHooker = Hooker {
        // Hook getItem() of base adapters
        XposedHelpers.findAndHookMethod(
            Classes.MMBaseAdapter,
            Methods.MMBaseAdapter_getItemInternal,
            C.Int,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val adapter = param.thisObject as BaseAdapter
                    val index = param.args[0] as Int
                    val record = ListViewHider.records[adapter] ?: return
                    synchronized(record) {
                        record.sections.forEach { section ->
                            if (index in section) {
                                param.args[0] = section.base + (index - section.start)
                                return
                            }
                        }
                    }
                }
            })

        // Hook getCount() of base adapters
        XposedHelpers.findAndHookMethod(
            Classes.MMBaseAdapter,
            "getCount",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val adapter = param.thisObject as BaseAdapter
                    val record = ListViewHider.records[adapter] ?: return
                    synchronized(record) {
                        if (record.sections.isNotEmpty()) {
                            param.result = record.sections.sumOf { it.size() }
                        }
                    }
                }
            })

        // Hook notifyDataSetChanged() of base adapters
        XposedHelpers.findAndHookMethod(
            C.BaseAdapter,
            "notifyDataSetChanged",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    when (param.thisObject::class.java) {
                        com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Classes.AddressAdapter -> {
                            updateAdapterSections(param)
                        }
                        com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.conversation.Classes.ConversationWithCacheAdapter -> {
                            updateAdapterSections(param)
                        }
                    }
                }
            })

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_RV_ADAPTER)
    }
}