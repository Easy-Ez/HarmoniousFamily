package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Classes.Utils
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Classes.XmlParser
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findMethodExact
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findMethodsByExactParameters
import java.lang.reflect.Method

object Methods {
    val XmlParser_parse: Method by wxLazy("XmlParser_parse") {
        getParseMethod()
    }

    val Util_getIntRandom: Method by wxLazy("Util_getIntRandom") {
        findMethodExact(Utils, "getIntRandom", C.Int, C.Int)
    }


    private fun getParseMethod(): Method? {
        return when {
            WechatGlobal.wxVersion!! >= Versions.v8_0_1 -> {
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