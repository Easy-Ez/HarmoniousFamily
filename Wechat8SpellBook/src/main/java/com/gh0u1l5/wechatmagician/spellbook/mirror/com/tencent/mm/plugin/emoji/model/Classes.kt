package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.emoji.model

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage.emotion.Classes.EmojiInfo
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil

object Classes {
    val EmojiMgrImpl: Class<*> by wxLazy("EmojiMgrImpl") {
        ReflectionUtil.findClassesFromPackage(
            WechatGlobal.wxLoader!!,
            WechatGlobal.wxClasses!!,
            "${WechatGlobal.wxPackageName}.plugin.emoji",
            1
        )
            .filterAnonymousClass()
            .filterByMethod(
                EmojiInfo,
                EmojiInfo
            )
            .firstOrNull()
    }
}