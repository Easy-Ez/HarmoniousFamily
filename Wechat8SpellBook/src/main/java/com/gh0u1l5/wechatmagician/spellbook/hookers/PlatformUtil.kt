package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IPlatformUtil
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Methods.Util_getIntRandom
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

object PlatformUtil : EventCenter() {
    override val interfaces: List<Class<*>>
        get() = listOf(IPlatformUtil::class.java)

    override fun provideEventHooker(event: String): Hooker {
        return when (event) {
            "onRPSInvoke", "onDiceInvoke" -> getIntRandomHooker
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    private val getIntRandomHooker = Hooker {
        XposedBridge.hookMethod(Util_getIntRandom, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val endIndex = param.args[0] as Int
                val startIndex = param.args[1] as Int
                val originResult = param.result as Int

                Log.d(
                    "Xposed-PlatformUtil",
                    "startIndex:${startIndex};endIndex:${endIndex};originResult:${originResult}"
                )
                when (endIndex) {
                    2 -> {
                        notifyForOperations("onRPSInvoke", param) { plugin ->
                            (plugin as IPlatformUtil).onRPSInvoke(originResult)
                        }
                    }
                    5 -> {
                        notifyForOperations("onDiceInvoke", param) { plugin ->
                            (plugin as IPlatformUtil).onDiceInvoke(originResult)
                        }
                    }
                }

            }
        })

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_GAME)
    }
}