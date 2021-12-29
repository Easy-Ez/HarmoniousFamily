package com.gh0u1l5.wechatmagician.spellbook.hookers

import androidx.recyclerview.widget.RecyclerView
import com.gh0u1l5.wechatmagician.spellbook.Predicate
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.data.Record
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.mvvmlist.Classes.MvvmRecyclerAdapter
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
        XposedHelpers.findAndHookMethod(MvvmRecyclerAdapter,"",)

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_RV_ADAPTER)
    }
}