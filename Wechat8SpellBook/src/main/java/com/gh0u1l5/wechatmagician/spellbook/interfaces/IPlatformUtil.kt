package com.gh0u1l5.wechatmagician.spellbook.interfaces

import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop

interface IPlatformUtil {
    companion object {
        /**
         * 剪刀✂️
         */
        val RPS_SCISSORS = 0

        /**
         * 石头
         */
        val RPS_ROCK = 1

        /**
         * 布
         */
        val RPS_PAPER = 2
    }


    /**
     * Rock paper scissors 猜拳游戏执行后的回调
     * 值只能是 [RPS_SCISSORS]..[RPS_PAPER]
     */
    fun onRPSInvoke(originResult: Int): Operation<Int> = nop()

    /**
     * 摇骰子执行后的回调
     * 值只能是 0..5 , 对于 1 点到 6 点
     */
    fun onDiceInvoke(originResult: Int): Operation<Int> = nop()
}