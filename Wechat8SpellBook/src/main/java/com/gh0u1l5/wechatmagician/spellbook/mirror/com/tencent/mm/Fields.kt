package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.Classes.ImgInfoStorage
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.Classes.LruCacheInterface
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.Classes.LruCacheWithListener
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findFieldsWithGenericType
import java.lang.reflect.Field

object Fields {
    val ImgInfoStorage_mBitmapCache: Field by wxLazy("ImgInfoStorage_mBitmapCache") {
        getBitmapCacheFiled()
    }


    private fun getBitmapCacheFiled(): Field? {
        return when {
            WechatGlobal.wxVersion!! >= Version("8.0.6") -> {
                findFieldsWithGenericType(
                    ImgInfoStorage,
                    "${LruCacheInterface.canonicalName}<java.lang.String, android.graphics.Bitmap>"
                )
                    .firstOrNull()?.apply { isAccessible = true }
            }
            else -> {
                findFieldsWithGenericType(
                    ImgInfoStorage,
                    "${LruCacheWithListener.canonicalName}<java.lang.String, android.graphics.Bitmap>"
                )
                    .firstOrNull()?.apply { isAccessible = true }
            }
        }
    }
}