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
        Log.d("Xposed-onDatabaseInserted", "table:${table},msg:$initialValues")
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
            "Xposed-onDatabaseExecuting",
            "sql:${sql} bindArgs:${bindArgs?.joinToString()} "
        )
        return super.onDatabaseExecuting(thisObject, sql, bindArgs, cancellationSignal)
    }
}