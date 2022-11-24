package cc.wecando.harmoniousfamily.frontend.widget

import android.content.Context
import android.util.AttributeSet
import androidx.preference.SwitchPreference
import cc.wecando.harmoniousfamily.R
import cc.wecando.harmoniousfamily.utils.PasswordUtil

class PasswordSwitchPreference : SwitchPreference {

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
        if (pref != null) {
            val encrypted = pref.getString("${key}_password", "")

            val status = pref.getBoolean(key, false)
            if (status) { // close
                if (encrypted.isNullOrEmpty()) {
                    return super.onClick()
                }
                val message = context.getString(R.string.prompt_verify_password)
                PasswordUtil.askPasswordWithVerify(context, "Wechat Magician", message, encrypted) {
                    super.onClick()
                }
            } else { // open
                if (encrypted.isNullOrEmpty()) {
                    return super.onClick()
                }
                PasswordUtil.createPassword(context, "Wechat Magician", pref, "${key}_password") {
                    super.onClick()
                }
            }
        }

    }
}