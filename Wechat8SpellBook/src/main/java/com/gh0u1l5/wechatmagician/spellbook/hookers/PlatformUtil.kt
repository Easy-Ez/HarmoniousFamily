package com.gh0u1l5.wechatmagician.spellbook.hookers

import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IPlatformUtil
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.emoji.model.Methods.EmojiMgrImpl_getRandomEmojiInfo
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Methods.Util_getIntRandom
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

object PlatformUtil : EventCenter() {

    private const val TYPE_ROCK_PAPER_SCISSORS = 2
    private const val TYPE_DICE = 5
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
                val endNumber = param.args[0] as Int
                val startNumber = param.args[1] as Int
                val originResult = param.result as Int

                // 因为是工具类中的方法, 这里为了更加保险起见, 判断调用栈是否来自表情包
                if (startNumber == 0 && BasicUtil.printStackTrace()
                        .contains("${EmojiMgrImpl_getRandomEmojiInfo.declaringClass.name}.${EmojiMgrImpl_getRandomEmojiInfo.name}")
                ) {

                    when (endNumber) {
                        TYPE_ROCK_PAPER_SCISSORS -> {
                            notifyForOperations("onRPSInvoke", param) { plugin ->
                                (plugin as IPlatformUtil).onRPSInvoke(originResult)
                            }
                        }
                        TYPE_DICE -> {
                            notifyForOperations("onDiceInvoke", param) { plugin ->
                                (plugin as IPlatformUtil).onDiceInvoke(originResult)
                            }
                        }
                    }
                }
            }
        })
        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_GAME)
    }
}