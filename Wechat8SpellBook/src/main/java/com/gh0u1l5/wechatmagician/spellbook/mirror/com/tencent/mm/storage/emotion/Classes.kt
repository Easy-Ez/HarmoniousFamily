package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage.emotion

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {

    val EmojiInfo: Class<*> by wxLazy("EmojiInfo") {

/*      情精确查找也行, 但是担心以后回混淆, 这里通过方法前面查找
        findClassIfExists(
            "${WechatGlobal.wxPackageName}.storage.emotion.EmojiInfo",
            WechatGlobal.wxLoader!!
        )
*/

        findClassesFromPackage(
            wxLoader!!,
            wxClasses!!,
            "${WechatGlobal.wxPackageName}.storage.emotion"
        )
            .filterByMethod(null, "setMd5", C.String)
            .firstOrNull()
    }

}