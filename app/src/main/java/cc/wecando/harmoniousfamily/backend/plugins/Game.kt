package cc.wecando.harmoniousfamily.backend.plugins

import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.replacement
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IPlatformUtil

object Game : IPlatformUtil {
    override fun onRPSInvoke(originResult: Int): Operation<Int> {
        return replacement(IPlatformUtil.RPS_PAPER)
    }

    override fun onDiceInvoke(originResult: Int): Operation<Int> {
        return replacement(5)
    }
}