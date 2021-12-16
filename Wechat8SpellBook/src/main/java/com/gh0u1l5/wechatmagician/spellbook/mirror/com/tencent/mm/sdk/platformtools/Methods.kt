package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Classes.XmlParser
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findMethodsByExactParameters
import java.lang.reflect.Method

object Methods {
    val XmlParser_parse: Method by wxLazy("XmlParser_parse") {
        getParseMethod()
    }


    private fun getParseMethod(): Method? {
        return when {
            WechatGlobal.wxVersion!! >= Version("8.0.1") -> {
                findMethodsByExactParameters(
                    XmlParser,
                    C.Map,
                    C.String,
                    C.String,
                    C.String
                ).firstOrNull()
            }
            else -> {
                findMethodsByExactParameters(XmlParser, C.Map, C.String, C.String).firstOrNull()
            }
        }
    }
}