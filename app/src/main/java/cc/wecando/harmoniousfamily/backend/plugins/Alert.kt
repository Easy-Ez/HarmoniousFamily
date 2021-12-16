package cc.wecando.harmoniousfamily.backend.plugins

import android.app.Activity
import android.widget.Toast
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IActivityHook

object Alert : IActivityHook {
    override fun onActivityStarting(activity: Activity) {
        Toast.makeText(activity, "Hello Wechat!", Toast.LENGTH_LONG).show()
    }
}