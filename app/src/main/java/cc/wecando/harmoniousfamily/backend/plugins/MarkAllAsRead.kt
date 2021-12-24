package cc.wecando.harmoniousfamily.backend.plugins

import android.app.Activity
import android.content.Context
import android.content.Intent
import cc.wecando.harmoniousfamily.Global.ITEM_ID_BUTTON_CLEAN_UNREAD
import cc.wecando.harmoniousfamily.Global.SETTINGS_MARK_ALL_AS_READ
import cc.wecando.harmoniousfamily.R
import cc.wecando.harmoniousfamily.backend.WechatHook
import cc.wecando.harmoniousfamily.backend.storage.Strings
import cc.wecando.harmoniousfamily.backend.storage.database.MainDatabase
import com.gh0u1l5.wechatmagician.spellbook.hookers.MenuAppender
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IPopupMenuHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.Classes.LauncherUI
import me.leolin.shortcutbadger.ShortcutBadger

object MarkAllAsRead : IPopupMenuHook {

    private val pref = WechatHook.settings

    private fun isPluginEnabled() = pref.getBoolean(SETTINGS_MARK_ALL_AS_READ, true)

    private fun cleanUnreadCount(activity: Activity?) {
        if (activity == null) {
            return
        }

        MainDatabase.cleanUnreadCount()
        ShortcutBadger.removeCount(activity)
        activity.finish()
        activity.startActivity(Intent(activity, LauncherUI).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        })
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onPopupMenuForConversationsCreating(username: String): List<MenuAppender.PopupMenuItem>? {
        if (!isPluginEnabled()) {
            return null
        }
        val itemId = ITEM_ID_BUTTON_CLEAN_UNREAD
        val title = Strings.getString(R.string.button_clean_unread)
        val onClickListener = { context: Context ->
            cleanUnreadCount(context as Activity)
        }
        return listOf(MenuAppender.PopupMenuItem(0, itemId, 0, title, onClickListener))
    }
}