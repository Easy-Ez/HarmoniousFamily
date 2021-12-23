package com.gh0u1l5.wechatmagician.spellbook.interfaces

import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop

interface IPlatformUtil {


    fun onRPSInvoke(originResult: Int): Operation<Int> = nop()

    fun onDiceInvoke(originResult: Int): Operation<Int> = nop()
}