package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm

import android.graphics.Bitmap
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Classes.LruCache
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val ImgInfoStorage: Class<*> by wxLazy("ImgInfoStorage") {
        getImgInfoStorageByRules()
    }

    val LruCacheWithListener: Class<*> by wxLazy("LruCacheWithListener") {
        getLruCacheWithListenerByRules()
    }

    val LruCacheInterface: Class<*> by wxLazy("LruCacheInterface") {
        LruCacheWithListener.interfaces.firstOrNull()
    }

    private fun getImgInfoStorageByRules(): Class<*>? {
        return when {
            WechatGlobal.wxVersion!! >= Version("8.0.1") -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, wxPackageName, 1)
                    .filterAnonymousClass()
                    .filterByMethod(
                        Bitmap::class.java,
                        C.String,
                        C.Boolean,
                        C.Float,
                        C.Boolean,
                        C.Boolean,
                        C.Boolean,
                        C.Int,
                        C.Boolean
                    )
                    .firstOrNull()
            }
            else -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, wxPackageName, 1)
                    .filterByMethod(C.String, C.String, C.String, C.String, C.Boolean)
                    .firstOrNull()
            }
        }


    }

    private fun getLruCacheWithListenerByRules(): Class<*>? {
        return when {
            WechatGlobal.wxVersion!! >= Version("8.0.1") -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, wxPackageName, 1)
                    .filterBySuper(LruCache)
                    .filterByInterfaceCount(1)
                    .firstOrNull()
            }
            else -> {
                findClassesFromPackage(wxLoader!!, wxClasses!!, wxPackageName, 1)
                    .filterBySuper(LruCache)
                    .firstOrNull()
            }
        }
    }
}