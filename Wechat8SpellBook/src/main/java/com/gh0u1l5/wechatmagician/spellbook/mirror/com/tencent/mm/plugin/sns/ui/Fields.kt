package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.Classes
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui.Classes.SnsEditTextInterface
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui.Classes.SnsUploadUI
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findFieldsWithType
import java.lang.reflect.Field

object Fields {
    val SnsUploadUI_mSnsEditText: Field by wxLazy("SnsUploadUI_mSnsEditText") {
        getSnsEditTextField()
    }


    private fun getSnsEditTextField(): Field? {
        return when {
            WechatGlobal.wxVersion!! >= Version("8.0.1") -> {
                findFieldsWithType(SnsUploadUI, SnsEditTextInterface.name)
                    .firstOrNull()?.apply { isAccessible = true }
            }
            else -> {
                findFieldsWithType(SnsUploadUI, "$wxPackageName.plugin.sns.ui.SnsEditText")
                    .firstOrNull()?.apply { isAccessible = true }
            }
        }
    }


}