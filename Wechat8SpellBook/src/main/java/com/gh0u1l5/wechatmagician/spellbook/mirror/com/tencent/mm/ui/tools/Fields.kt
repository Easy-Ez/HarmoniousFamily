package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.tools

import android.widget.EditText
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.tools.Classes.ActionBarSearchView
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findDeclaredFieldsWithType
import java.lang.reflect.Field

object Fields {
    val ActionBarSearchView_mEditText: Field by wxLazy(
        "ActionBarSearchView_mEditText",
        Versions.v8_0_30
    ) {
        findDeclaredFieldsWithType(ActionBarSearchView, EditText::class.java).firstOrNull()
            ?.apply { isAccessible = true }
    }


}