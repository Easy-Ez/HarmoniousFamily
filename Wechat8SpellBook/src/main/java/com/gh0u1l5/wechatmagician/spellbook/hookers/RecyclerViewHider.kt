package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.Predicate
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.data.Record
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Classes
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
        return listOf(WxRecyclerAdapterHooker)
    }

    private val WxRecyclerAdapterHooker = Hooker {
        /**
         *  构造 WxRecyclerAdapter 时会传入 itemConvertFactory
         *  bindViewHolder 时候, 会通过  ItemConvertFactory 取 ItemConvert
         *  这里拦截 ItemConvert 的 onBindViewHolder 方法
         */
        XposedHelpers.findAndHookMethod("com.tencent.mm.view.recyclerview.f",
            WechatGlobal.wxLoader,
            "a",
            Classes.WxViewHolder,
            Classes.ConvertData,
            C.Int,
            C.Int,
            C.Boolean,
            C.List,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    param?.let {
                        val simpleViewHolder = param.args[0]
                        val item = param.args[1]
                        val position = param.args[2]
                        val itemType = param.args[3]
                        val isHotPatch = param.args[4]
                        Log.d(
                            "Xposed-convert",
                            "AddressItemConvert ,vh:${simpleViewHolder};item:${item};position:${position}itemType:${itemType}"
                        )
                    }

                }
            })

//        XposedBridge.hookMethod(
//            Methods.AddressItemConvert_onBindViewHolder, object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    val vh = param.args[0]
//                    val item = param.args[1]
//                    val position = param.args[2]
//                    val itemType = param.args[3]
//                    Log.d(
//                        "Xposed-convert",
//                        "AddressItemConvert ,vh:${vh};item:${item};position:${position}itemType:${itemType}"
//                    )
//                    for (filed in vh.javaClass.fields) {
//                        Log.d(
//                            "Xposed-convert",
//                            "filed name:${filed.name};type:${filed.type};value:${filed.get(vh)}"
//                        )
//                        if (filed.type == View::class.java) {
//                            val itemView = (filed.get(vh) as View)
//                            val positionNew = vh.javaClass.getMethod("getPosition")
//                                .invoke(vh)
//                            if (positionNew != -1) {
//                                val visible = positionNew != 1
//                                itemView.visibility = if (visible) View.VISIBLE else View.GONE
//                                val params = itemView.layoutParams as ViewGroup.LayoutParams
//                                Log.d(
//                                    "Xposed-convert",
//                                    "params ${params.height}"
//                                )
//                                if (visible) {
//                                    params.height =
//                                        ViewGroup.LayoutParams.WRAP_CONTENT // 根据具体需求场景设置
//                                    params.width = ViewGroup.LayoutParams.MATCH_PARENT
//                                } else {
//                                    params.height = 0
//                                    params.width = 0
//                                }
//                                itemView.layoutParams = params
//                            }
//                        }
//                    }
//                }
//            }
//        )
    }
}