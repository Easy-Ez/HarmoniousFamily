package com.gh0u1l5.wechatmagician.spellbook.data

import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.mirror.androidx.recyclerview.widget.Fields.Adapter_Observable

/**
 * 原始方法被混淆, 需要使用反射的方式调用,提供该类便于外部调用
 */
class InnerAdapter(val adapter: Any) {

    fun notifyDataSetChanged() {
        val mObservable = Adapter_Observable.get(adapter)
        val method = Adapter_Observable.type.getMethod("notifyChanged")
        Log.d("InnerAdapter", "mObservable:${mObservable},method:${method}")
        method.invoke(mObservable)
    }
}