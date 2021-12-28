package cc.wecando.harmoniousfamily.backend.plugins

import cc.wecando.harmoniousfamily.Global
import cc.wecando.harmoniousfamily.backend.WechatHook
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.replacement
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IPlatformUtil

object Game : IPlatformUtil {
    private val pref = WechatHook.settings
    override fun onRPSInvoke(originResult: Int): Operation<Int> {
        val rpsFlag = pref.getBoolean(Global.SETTINGS_GAME_RPS_FLAG, false)
        if (rpsFlag) {
            val replaceInt = pref.getInt(
                Global.SETTINGS_GAME_RPS,
                -1
            )
            if (replaceInt in IPlatformUtil.RPS_SCISSORS until IPlatformUtil.RPS_PAPER) {
                return replacement(replaceInt)
            }

        }
        return super.onRPSInvoke(originResult)
    }

    override fun onDiceInvoke(originResult: Int): Operation<Int> {
        val rpsFlag = pref.getBoolean(Global.SETTINGS_GAME_DICE_FLAG, false)
        if (rpsFlag) {
            val replaceInt = pref.getInt(
                Global.SETTINGS_GAME_DICE,
                -1
            )
            if (replaceInt in 0..5) {
                return replacement(replaceInt)
            }
        }
        return super.onDiceInvoke(originResult)
    }
}