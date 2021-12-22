package cc.wecando.harmoniousfamily.backend.plugins

import android.content.ContentValues
import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IDatabaseHook

object Message : IDatabaseHook {

    override fun onDatabaseInserted(
        thisObject: Any,
        table: String,
        nullColumnHack: String?,
        initialValues: ContentValues?,
        conflictAlgorithm: Int,
        result: Long?
    ): Operation<Long> {
        Log.d("Xposed", "table:${table},msg:$initialValues")
        if (table == "message") {
            Log.d("Xposed", "New Message: $initialValues")
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

    override fun onDatabaseExecuting(
        thisObject: Any,
        sql: String,
        bindArgs: Array<*>?,
        cancellationSignal: Any?
    ): Boolean {
        Log.d(
            "Xposed",
            "thisObject:${thisObject} sql:${sql} bindArgs:$bindArgs "
        )
        return super.onDatabaseExecuting(thisObject, sql, bindArgs, cancellationSignal)
    }
}