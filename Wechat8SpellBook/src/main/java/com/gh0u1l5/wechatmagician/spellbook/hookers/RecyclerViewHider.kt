package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gh0u1l5.wechatmagician.spellbook.Predicate
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.data.Record
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.view.recyclerview.Methods.WxRecyclerAdapter_getOnItemConvertClickListener
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
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
                            "AddressItemConvert ,itemView:${itemView};item:${item};positionForList:${positionForList}"
                        )
                        toggleItemVisible(itemView, item, positionForList)
                    }

                }

                private fun toggleItemVisible(itemView: View, item: Any?, positionForList: Int) {
                    if (positionForList != -1) {
                        // todo 这里插入判断  VISIBLE or GONE 逻辑
                        val visible = records[Any()]?.predicates?.values?.any { it(item) } ?: true

                        itemView.visibility = if (visible) View.VISIBLE else View.GONE
                        val params = itemView.layoutParams as ViewGroup.LayoutParams
                        Log.d(
                            "Xposed-convert",
                            "params ${params.height}"
                        )
                        if (visible) {
                            params.height =
                                ViewGroup.LayoutParams.WRAP_CONTENT // 根据具体需求场景设置
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        } else {
                            params.height = 0
                            params.width = 0
                        }
                        itemView.layoutParams = params
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