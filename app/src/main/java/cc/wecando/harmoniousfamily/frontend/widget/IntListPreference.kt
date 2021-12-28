package cc.wecando.harmoniousfamily.frontend.widget

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference

class IntListPreference : ListPreference {

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

    override fun persistString(value: String): Boolean {
        return persistInt(Integer.parseInt(value))
    }

    override fun getPersistedString(defaultReturnValue: String?): String {
        val intValue: Int = if (defaultReturnValue != null) {
            getPersistedInt(defaultReturnValue.toInt())
        } else {
            getPersistedInt(0)
        }

        return intValue.toString()
    }

}