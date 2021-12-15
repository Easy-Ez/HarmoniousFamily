package cc.wecando.harmoniousfamily.frontend.widget

import android.content.Context
import android.util.AttributeSet
import cc.wecando.harmoniousfamily.utils.PasswordUtil

class PasswordPreference : androidx.preference.EditTextPreference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onClick() {
        val pref = preferenceManager.sharedPreferences
        val encrypted = pref.getString(key, "")
        if (encrypted.isNullOrEmpty()) {
            PasswordUtil.createPassword(context, "Wechat Magician", pref, key)
        } else {
            PasswordUtil.changePassword(context, "Wechat Magician", pref, key)
        }
    }
}