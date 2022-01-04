package com.gh0u1l5.wechatmagician.spellbook.util

import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage.Classes
import de.robv.android.xposed.XposedHelpers

object FiledHelper {
    /**
     * 从通讯录 Item bean(ContactInfo) 中取 username
     */
    fun getUserNameFromContactInfo(contactInstance: Any?): String? {
        val userNameOfContactInfo = contactInstance?.let {
            val contactInfoField =
                XposedHelpers.findFirstFieldByExactType(
                    contactInstance::class.java,
                    Classes.ContactInfo
                )
            contactInfoField.get(contactInstance)
            val contactInfo = contactInfoField.get(contactInstance)
            try {
                XposedHelpers.getObjectField(contactInfo, "field_username") as String?
            } catch (e: Throwable) {
                null
            }
        }
        Log.d("FiledHelper", "userNameOfContactInfo:$userNameOfContactInfo")
        return userNameOfContactInfo
    }
}