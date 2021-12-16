package cc.wecando.harmoniousfamily.backend.plugins

import android.content.ContentValues
import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IDatabaseHook
import de.robv.android.xposed.XposedBridge.log

object Message : IDatabaseHook {
    override fun onDatabaseOpened(
        path: String,
        factory: Any?,
        flags: Int,
        errorHandler: Any?,
        result: Any?
    ): Operation<Any> {
        return super.onDatabaseOpened(path, factory, flags, errorHandler, result)
    }
    override fun onDatabaseInserted(
        thisObject: Any,
        table: String,
        nullColumnHack: String?,
        initialValues: ContentValues?,
        conflictAlgorithm: Int,
        result: Long?
    ): Operation<Long> {
        Log.d("xposed", "table:${table},msg:$initialValues")
        if (table == "message") {
            log("New Message: $initialValues")
        }
        return super.onDatabaseInserted(
            thisObject,
            table,
            nullColumnHack,
            initialValues,
            conflictAlgorithm,
            result
        )
    }
}