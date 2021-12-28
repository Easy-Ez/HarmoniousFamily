package cc.wecando.harmoniousfamily.utils

import android.content.Context
import cc.wecando.harmoniousfamily.R

object GameUtils {

    /**
     * 游戏-剪刀石头布
     * 根据存入 sp 中的值, 获取对应的文案
     */
    fun getRPSTextByValue(value: Int, context: Context): String {
        val stringArray = context.resources.getStringArray(R.array.pref_option_module_rps_entry)
        return stringArray[value]
    }
}