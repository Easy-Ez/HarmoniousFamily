package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.emoji.model

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.emoji.model.Classes.EmojiMgrImpl
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage.emotion.Classes.EmojiInfo
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findMethodsByExactParameters
import java.lang.reflect.Method

object Methods {

    val EmojiMgrImpl_getRandomEmojiInfo: Method by wxLazy("getRandomEmojiInfo") {
        findMethodsByExactParameters(
            EmojiMgrImpl,
            EmojiInfo,
            EmojiInfo
        ).firstOrNull()

    }
}