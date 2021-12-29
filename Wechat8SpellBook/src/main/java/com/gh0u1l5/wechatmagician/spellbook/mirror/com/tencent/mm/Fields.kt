package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
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
            // 新版本 ImgInfoStorage_mBitmapCache 是利用动态, 类型是接口.所以要先找到接口
            WechatGlobal.wxVersion!! >= Versions.v8_0_1 -> {
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