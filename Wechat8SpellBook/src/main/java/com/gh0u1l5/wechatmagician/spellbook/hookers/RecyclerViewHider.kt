package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.gh0u1l5.wechatmagician.spellbook.Predicate
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.data.InnerAdapter
import com.gh0u1l5.wechatmagician.spellbook.data.Record
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Methods.WxRecyclerAdapter_getOnItemConvertClickListener
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.util.concurrent.ConcurrentHashMap

object RecyclerViewHider : HookerProvider {

    private val records: MutableMap<InnerAdapter, Record> = ConcurrentHashMap()

    fun register(
        adapter: InnerAdapter,
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


    override fun provideStaticHookers(): List<Hooker> {
        return listOf(WxRecyclerAdapterHooker)
    }

    private val WxRecyclerAdapterHooker = Hooker {
        /**
         *  构造 WxRecyclerAdapter 时会传入 itemConvertFactory
         *  bindViewHolder 时候, 会通过  ItemConvertFactory 取 ItemConvert
         *  这里拦截 ItemConvert 的 onBindViewHolder 方法
         */
        XposedBridge.hookMethod(
            WxRecyclerAdapter_getOnItemConvertClickListener,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    param?.let {
                        val itemView = param.args[0] as View
                        val item = param.args[1]
                        val positionForList = param.args[2] as Int
                        Log.d(
                            "Xposed-convert",
                            "AddressItemConvert ,thisObject:${param.thisObject};itemView:${itemView};item:${item};positionForList:${positionForList}"
                        )
                        toggleItemVisible(param.thisObject, itemView, item, positionForList)
                    }

                }

                private fun toggleItemVisible(
                    adapter: Any,
                    itemView: View,
                    item: Any?,
                    positionForList: Int
                ) {
                    if (positionForList != -1) {
                        val gone =
                            records.firstNotNullOfOrNull {
                                if (it.key.adapter == adapter) {
                                    it.value
                                } else {
                                    null
                                }
                            }?.predicates?.values?.any {
                                it(item)
                            } ?: false

                        itemView.visibility = if (gone) View.GONE else  View.VISIBLE
                        val params = itemView.layoutParams as ViewGroup.LayoutParams
                        if (gone) {
                            params.height = 0
                            params.width = 0
                        } else {
                            params.height =
                                ViewGroup.LayoutParams.WRAP_CONTENT // 根据具体需求场景设置
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        }
                        itemView.layoutParams = params
                    }
                }
            })
    }
}