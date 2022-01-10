package cc.wecando.harmoniousfamily.backend.plugins

import android.content.ContentValues
import cc.wecando.harmoniousfamily.Global
import cc.wecando.harmoniousfamily.R
import cc.wecando.harmoniousfamily.backend.WechatHook
import cc.wecando.harmoniousfamily.backend.storage.Strings
import cc.wecando.harmoniousfamily.backend.storage.list.SnsBlacklist
import cc.wecando.harmoniousfamily.utils.MessageUtil
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IDatabaseHook

object AntiSnsDelete : IDatabaseHook {

    private val pref = WechatHook.settings

    override fun onDatabaseUpdating(thisObject: Any, table: String, values: ContentValues, whereClause: String?, whereArgs: Array<String>?, conflictAlgorithm: Int): Operation<Int> {
        when (table) {
            "SnsInfo" -> { // delete moment
                if (values["type"] !in listOf(1, 2, 3, 15)) {
                    return nop()
                }
                if (values["sourceType"] != 0) {
                    return nop()
                }
                if (values["stringSeq"] in SnsBlacklist) {
                    return nop()
                }
                if (!pref.getBoolean(Global.SETTINGS_SNS_DELETE_MOMENT, true)) {
                    return nop()
                }
                val content = values["content"] as ByteArray?
                handleMomentDelete(content, values)
            }
            "SnsComment" -> { // delete moment comment
                if (values["type"] == 1) {
                    return nop()
                }
                if (values["commentflag"] != 1) {
                    return nop()
                }
                if (!pref.getBoolean(Global.SETTINGS_SNS_DELETE_COMMENT, true)) {
                    return nop()
                }
                val curActionBuf = values["curActionBuf"] as ByteArray?
                handleCommentDelete(curActionBuf, values)
            }
        }
        return nop()
    }

    // handleMomentDelete notifies user that someone has deleted the given moment.
    private fun handleMomentDelete(content: ByteArray?, values: ContentValues) {
        val label = Strings.getString(R.string.label_deleted)
        MessageUtil.notifyInfoDelete(label, content)?.let { msg ->
            values.remove("sourceType")
            values.put("content", msg)
        }
    }

    // handleCommentDelete notifies user that someone has deleted the given comment in moments.
    private fun handleCommentDelete(curActionBuf: ByteArray?, values: ContentValues) {
        val label = Strings.getString(R.string.label_deleted)
        MessageUtil.notifyCommentDelete(label, curActionBuf)?.let { msg ->
            values.remove("commentflag")
            values.put("curActionBuf", msg)
        }
    }
}