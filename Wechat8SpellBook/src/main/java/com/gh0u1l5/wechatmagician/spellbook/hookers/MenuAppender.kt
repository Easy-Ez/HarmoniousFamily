package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.widget.AdapterView
import com.gh0u1l5.wechatmagician.spellbook.BuildConfig
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IPopupMenuHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage.Classes
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.base.Classes.MMListPopupWindow
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Methods
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.contact.Methods.ContactLongClickListener_onCreateContextMenu
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.conversation.Classes.ConversationCreateContextMenuListener
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.conversation.Classes.ConversationLongClickListener
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookMethod
import de.robv.android.xposed.XposedHelpers.*

object MenuAppender : EventCenter() {

    override val interfaces: List<Class<*>>
        get() = listOf(IPopupMenuHook::class.java)

    data class PopupMenuItem(
        val groupId: Int,
        val itemId: Int,
        val order: Int,
        val title: String,
        val onClickListener: (context: Context) -> Unit
    )

    @Volatile
    var currentUsername: String? = null

    @Volatile
    var currentMenuItems: List<PopupMenuItem>? = null

    override fun provideStaticHookers(): List<Hooker> {
        return listOf(onMMListPopupWindowShowHooker, onMMListPopupWindowDismissHooker)
    }

    override fun provideEventHooker(event: String): Hooker {
        return when (event) {
            "onPopupMenuForContactsCreating" -> onPopupMenuForContactsCreateHooker
            "onPopupMenuForConversationsCreating" -> onPopupMenuForConversationsCreateHooker
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    private val onMMListPopupWindowShowHooker = Hooker {
        findAndHookMethod(MMListPopupWindow, "show", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val listenerField =
                    findFirstFieldByExactType(MMListPopupWindow, C.AdapterView_OnItemClickListener)
                val listener =
                    listenerField.get(param.thisObject) as AdapterView.OnItemClickListener
                listenerField.set(
                    param.thisObject,
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val title = parent.adapter.getItem(position)
                        val context = getObjectField(param.thisObject, "mContext") as Context
                        currentMenuItems?.forEach {
                            if (title == it.title) {
                                it.onClickListener(context)
                            }
                        }
                        listener.onItemClick(parent, view, position, id)
                    })
            }
        })
    }

    private val onMMListPopupWindowDismissHooker = Hooker {
        findAndHookMethod(MMListPopupWindow, "dismiss", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                currentUsername = null
                currentMenuItems = null
            }
        })
    }

    /**
     * 拦截通讯录的 PopupMenu 创建, 获取 currentUsername
     */
    private val onPopupMenuForContactsCreateHooker = Hooker {
        hookMethod(
            Methods.ContactLongClickListener_onItemLongClick, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    handleItemLongClick(param)
                }

                /**
                 * 拦截通讯录长按事件, 获取当前 item 的 userName
                 */
                private fun handleItemLongClick(param: MethodHookParam) {
                    val contactInfo = if (WechatGlobal.wxVersion!! >= Versions.v8_0_16) {
                        val itemView = param.args[0] as View?
                        val item = param.args[1]
                        val position = param.args[2] as Int
                        Log.d(
                            "Xposed-secret",
                            "onItemLongClick position:${position};itemView: $itemView"
                        )
                        val contactInfoField =
                            findFirstFieldByExactType(item::class.java, Classes.ContactInfo)
                        contactInfoField.get(item)
                    } else {
                        val parent = param.args[0] as AdapterView<*>
                        val position = param.args[2] as Int
                        val item = parent.adapter?.getItem(position)
                        item
                    }
                    if (BuildConfig.DEBUG)
                        contactInfo?.let { addressUIContactItem ->
                            addressUIContactItem::class.java.declaredFields.forEach { filed ->
                                filed.isAccessible = true
                                Log.d(
                                    "Xposed-secret",
                                    "field name:${filed.name};value: ${
                                        filed.get(
                                            addressUIContactItem
                                        )
                                    }"
                                )
                            }
                        }
                    currentUsername = getObjectField(contactInfo, "field_username") as String?
                    Log.d(
                        "Xposed-secret", "currentUsername:${currentUsername}"
                    )

                }
            })




        hookMethod(
            ContactLongClickListener_onCreateContextMenu, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val menu = param.args[0] as ContextMenu
                    val view = param.args[1] as View
                    currentMenuItems =
                        notifyForResults("onPopupMenuForContactsCreating") { plugin ->
                            (plugin as IPopupMenuHook).onPopupMenuForContactsCreating(
                                currentUsername ?: ""
                            )
                        }.flatten().sortedBy { it.itemId }

                    currentMenuItems?.forEach {
                        val item = menu.add(it.groupId, it.itemId, it.order, it.title)
                        item.setOnMenuItemClickListener { _ ->
                            it.onClickListener(view.context)
                            return@setOnMenuItemClickListener true
                        }
                    }
                }
            })

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_CONTACT_POPUP)
    }

    /**
     * 拦截聊天列表的 PopupMenu 创建
     */
    private val onPopupMenuForConversationsCreateHooker = Hooker {
        findAndHookMethod(
            ConversationLongClickListener, "onItemLongClick",
            C.AdapterView, C.View, C.Int, C.Long, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val parent = param.args[0] as AdapterView<*>
                    val position = param.args[2] as Int
                    val item = parent.adapter?.getItem(position)
                    currentUsername = getObjectField(item, "field_username") as String?
                }
            })

        findAndHookMethod(
            ConversationCreateContextMenuListener, "onCreateContextMenu",
            C.ContextMenu, C.View, C.ContextMenuInfo, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val menu = param.args[0] as ContextMenu

                    currentMenuItems =
                        notifyForResults("onPopupMenuForConversationsCreating") { plugin ->
                            (plugin as IPopupMenuHook).onPopupMenuForConversationsCreating(
                                currentUsername ?: ""
                            )
                        }.flatten().sortedBy { it.itemId }

                    currentMenuItems?.forEach {
                        val item = menu.add(it.groupId, it.itemId, it.order, it.title)
                        item.setOnMenuItemClickListener { _ ->
                            it.onClickListener(param.thisObject as Activity)
                            return@setOnMenuItemClickListener true
                        }
                    }
                }
            })

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_CONVERSATION_POPUP)
    }
}