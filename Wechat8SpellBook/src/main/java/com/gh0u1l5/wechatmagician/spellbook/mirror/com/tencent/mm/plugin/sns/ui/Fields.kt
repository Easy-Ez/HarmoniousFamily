package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui.Classes.SnsEditTextInterface
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui.Classes.SnsUploadUI
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findDeclaredFieldsWithType
import java.lang.reflect.Field

object Fields {
    val SnsUploadUI_mSnsEditText: Field by wxLazy("SnsUploadUI_mSnsEditText") {
        getSnsEditTextField()
    }


    private fun getSnsEditTextField(): Field? {
        return when {
            WechatGlobal.wxVersion!! >= Versions.v8_0_1 -> {
                findDeclaredFieldsWithType(SnsUploadUI, SnsEditTextInterface.name)
                    .firstOrNull()?.apply { isAccessible = true }
            }
            else -> {
                findDeclaredFieldsWithType(SnsUploadUI, "$wxPackageName.plugin.sns.ui.SnsEditText")
                    .firstOrNull()?.apply { isAccessible = true }
            }
        }
    }


}