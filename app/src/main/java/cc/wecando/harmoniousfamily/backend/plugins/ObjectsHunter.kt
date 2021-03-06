package cc.wecando.harmoniousfamily.backend.plugins

import android.content.ContentValues
import android.widget.BaseAdapter
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.AddressAdapterObject
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.ConversationAdapterObject
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.ConversationAdapterObjectNew
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.ImgStorageObject
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.MMSearchContactAdapterObject
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.MainDatabaseObject
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.MsgStorageObject
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.RecentConversationAdapterObject
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.SnsDatabaseObject
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop
import com.gh0u1l5.wechatmagician.spellbook.data.InnerAdapter
import com.gh0u1l5.wechatmagician.spellbook.interfaces.*
import java.lang.ref.SoftReference

object ObjectsHunter : IActivityHook, IAdapterHook, IDatabaseHook, IMessageStorageHook,
    IImageStorageHook {

    // TODO: hook more objects in this plugin

    override fun onMessageStorageCreated(storage: Any) {
        if (MsgStorageObject !== storage) {
            MsgStorageObject = storage
        }
    }

    override fun onImageStorageCreated(storage: Any) {
        if (ImgStorageObject !== storage) {
            ImgStorageObject = storage
        }
    }

    override fun onAddressAdapterCreated(adapter: BaseAdapter) {
        AddressAdapterObject = SoftReference(adapter)
    }

    override fun onConversationAdapterCreated(adapter: BaseAdapter) {
        ConversationAdapterObject = SoftReference(adapter)
    }

    override fun onRecentConversationAdapterCreated(adapter: BaseAdapter) {
        RecentConversationAdapterObject = SoftReference(adapter)
    }

    override fun onMMSearchContactAdapterCreated(adapter: BaseAdapter) {
        MMSearchContactAdapterObject = SoftReference(adapter)
    }

    override fun onAddressAdapterCreated(adapter: InnerAdapter) {
        ConversationAdapterObjectNew = SoftReference(adapter)
    }

    override fun onDatabaseOpened(
        path: String,
        password: String,
        factory: Any?,
        flags: Int,
        errorHandler: Any?,
        result: Any?
    ): Operation<Any> {
        if (path.endsWith("SnsMicroMsg.db")) {
            if (SnsDatabaseObject !== result) {
                SnsDatabaseObject = result
            }
        }
        return nop()
    }

    override fun onDatabaseUpdated(
        thisObject: Any,
        table: String,
        values: ContentValues,
        whereClause: String?,
        whereArgs: Array<String>?,
        conflictAlgorithm: Int,
        result: Int
    ): Operation<Int> {
        val path = thisObject.toString()
        if (path.endsWith("EnMicroMsg.db")) {
            if (MainDatabaseObject !== thisObject) {
                MainDatabaseObject = thisObject
            }
        }
        return nop()
    }
}