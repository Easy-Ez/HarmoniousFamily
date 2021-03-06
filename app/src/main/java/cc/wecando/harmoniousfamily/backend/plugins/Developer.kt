package cc.wecando.harmoniousfamily.backend.plugins

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListAdapter
import cc.wecando.harmoniousfamily.Global.DEVELOPER_DATABASE_DELETE
import cc.wecando.harmoniousfamily.Global.DEVELOPER_DATABASE_EXECUTE
import cc.wecando.harmoniousfamily.Global.DEVELOPER_DATABASE_INSERT
import cc.wecando.harmoniousfamily.Global.DEVELOPER_DATABASE_QUERY
import cc.wecando.harmoniousfamily.Global.DEVELOPER_DATABASE_UPDATE
import cc.wecando.harmoniousfamily.Global.DEVELOPER_TRACE_FILES
import cc.wecando.harmoniousfamily.Global.DEVELOPER_TRACE_LOGCAT
import cc.wecando.harmoniousfamily.Global.DEVELOPER_UI_DUMP_POPUP_MENU
import cc.wecando.harmoniousfamily.Global.DEVELOPER_UI_TOUCH_EVENT
import cc.wecando.harmoniousfamily.Global.DEVELOPER_UI_TRACE_ACTIVITIES
import cc.wecando.harmoniousfamily.Global.DEVELOPER_XML_PARSER
import cc.wecando.harmoniousfamily.backend.WechatHook
import cc.wecando.harmoniousfamily.utils.MessageUtil.argsToString
import cc.wecando.harmoniousfamily.utils.MessageUtil.bundleToString
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Classes.Logcat
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Methods.XmlParser_parse
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.base.Classes.MMListPopupWindow
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.database.Classes.SQLiteCursorFactory
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.database.Classes.SQLiteDatabase
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.support.Classes.SQLiteCancellationSignal
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.*
import de.robv.android.xposed.XposedHelpers.findAndHookConstructor
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import java.io.File

object Developer : HookerProvider {
    const val TAG = "Developer"
    private val pref = WechatHook.developer

    override fun provideStaticHookers(): List<Hooker> {
        return listOf(
            traceTouchEventsHooker,
            traceActivitiesHooker,
            dumpPopupMenuHooker,
            traceDatabaseHooker,
            traceLogcatHooker,
            traceFilesHooker,
            traceXMLParseHooker
        )
    }

    // Hook View.onTouchEvent to trace touch events.
    private val traceTouchEventsHooker = Hooker {
        if (pref.getBoolean(DEVELOPER_UI_TOUCH_EVENT, false)) {
            findAndHookMethod(C.View, "onTouchEvent", C.MotionEvent, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    Log.d(
                        "Xposed",
                        "View.onTouchEvent => obj.class = ${param.thisObject::class.java}"
                    )
                }
            })
        }
    }

    // Hook Activity.startActivity and Activity.onCreate to trace activities.
    private val traceActivitiesHooker = Hooker {
        if (pref.getBoolean(DEVELOPER_UI_TRACE_ACTIVITIES, false)) {
            findAndHookMethod(C.Activity, "startActivity", C.Intent, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val intent = param.args[0] as Intent?
                    Log.d(
                        "${TAG}-startActivity",
                        "Activity.startActivity => " +
                                "${param.thisObject::class.java}, " +
                                "intent => ${bundleToString(intent?.extras)}"
                    )
                }
            })

            findAndHookMethod(C.Activity, "onCreate", C.Bundle, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val bundle = param.args[0] as Bundle?
                    val intent = (param.thisObject as Activity).intent
                    Log.d(
                        "${TAG}-onCreate",
                        "Activity.onCreate => " +
                                "${param.thisObject::class.java}, " +
                                "intent => ${bundleToString(intent?.extras)}, " +
                                "bundle => ${bundleToString(bundle)}"
                    )
                }
            })
        }
    }

    // Hook MMListPopupWindow to trace every popup menu.
    private val dumpPopupMenuHooker = Hooker {
        if (pref.getBoolean(DEVELOPER_UI_DUMP_POPUP_MENU, false)) {
            hookAllConstructors(MMListPopupWindow, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val menu = param.thisObject
                    val context = param.args[0]
                    Log.d(
                        "${TAG}-POPUP", "POPUP => menu.class = ${menu::class.java}"
                    )
                    Log.d(
                        "${TAG}-POPUP", "POPUP => context.class = ${context::class.java}"
                    )
                }
            })

            findAndHookMethod(
                MMListPopupWindow,
                "setAdapter",
                C.ListAdapter,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val adapter = param.args[0] as ListAdapter? ?: return
                        Log.d("Xposed", "POPUP => adapter.count = ${adapter.count}")
                        (0 until adapter.count).forEach { index ->
                            Log.d(
                                "${TAG}-POPUP",
                                "POPUP => adapter.item[$index] = ${adapter.getItem(index)}"
                            )
                            Log.d(
                                "${TAG}-POPUP",
                                "POPUP => adapter.item[$index].class = ${adapter.getItem(index)::class.java}"
                            )
                        }
                    }
                })
        }
    }

    // Hook SQLiteDatabase to trace all the database operations.
    private val traceDatabaseHooker = Hooker {
        if (pref.getBoolean(DEVELOPER_DATABASE_QUERY, false)) {
            findAndHookMethod(
                SQLiteDatabase,
                "rawQueryWithFactory",
                SQLiteCursorFactory,
                C.String,
                C.StringArray,
                C.String,
                SQLiteCancellationSignal,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val sql = param.args[1] as String?
                        val selectionArgs = param.args[2] as Array<*>?
                        Log.d(
                            "${TAG}-db",
                            "DB => query sql = $sql, selectionArgs = ${argsToString(selectionArgs)}, db = ${param.thisObject}"
                        )
                    }
                })
        }

        if (pref.getBoolean(DEVELOPER_DATABASE_INSERT, false)) {
            findAndHookMethod(
                SQLiteDatabase, "insertWithOnConflict",
                C.String, C.String, C.ContentValues, C.Int, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val table = param.args[0] as String?
                        val values = param.args[2] as ContentValues?
                        Log.d(
                            "${TAG}-db",
                            "DB => insert table = $table, values = $values, db = ${param.thisObject}"
                        )
                    }
                })
        }

        if (pref.getBoolean(DEVELOPER_DATABASE_UPDATE, false)) {
            findAndHookMethod(
                SQLiteDatabase,
                "updateWithOnConflict",
                C.String,
                C.ContentValues,
                C.String,
                C.StringArray,
                C.Int,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val table = param.args[0] as String?
                        val values = param.args[1] as ContentValues?
                        val whereClause = param.args[2] as String?
                        val whereArgs = param.args[3] as Array<*>?
                        Log.d(
                            "${TAG}-db",
                            "DB => update " +
                                    "table = $table, " +
                                    "values = $values, " +
                                    "whereClause = $whereClause, " +
                                    "whereArgs = ${argsToString(whereArgs)}, " +
                                    "db = ${param.thisObject}"
                        )
                    }
                })
        }

        if (pref.getBoolean(DEVELOPER_DATABASE_DELETE, false)) {
            findAndHookMethod(
                SQLiteDatabase, "delete",
                C.String, C.String, C.StringArray, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val table = param.args[0] as String?
                        val whereClause = param.args[1] as String?
                        val whereArgs = param.args[2] as Array<*>?
                        Log.d(
                            "${TAG}-db",
                            "DB => delete " +
                                    "table = $table, " +
                                    "whereClause = $whereClause, " +
                                    "whereArgs = ${argsToString(whereArgs)}, " +
                                    "db = ${param.thisObject}"
                        )
                    }
                })
        }

        if (pref.getBoolean(DEVELOPER_DATABASE_EXECUTE, false)) {
            findAndHookMethod(
                SQLiteDatabase, "executeSql",
                C.String, C.ObjectArray, SQLiteCancellationSignal, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val sql = param.args[0] as String?
                        val bindArgs = param.args[1] as Array<*>?
                        Log.d(
                            "${TAG}-db",
                            "DB => executeSql sql = $sql, bindArgs = ${argsToString(bindArgs)}, db = ${param.thisObject}"
                        )
                    }
                })
        }
    }

    // Hook Log to trace hidden logcat output.
    private val traceLogcatHooker = Hooker {

        if (pref.getBoolean(DEVELOPER_TRACE_LOGCAT, false)) {
            val functions = listOf("d", "e", "f", "i", "v", "w")
            functions.forEach { func ->
                findAndHookMethod(
                    Logcat,
                    func,
                    C.String,
                    C.String,
                    C.ObjectArray,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val tag = param.args[0] as String?
                            val msg = param.args[1] as String?
                            val args = param.args[2] as Array<*>?
                            if (args == null) {
                                Log.d(
                                    "${TAG}-log", "LOG.${func.uppercase()} => [$tag] $msg"
                                )
                            } else {
                                Log.d(
                                    "${TAG}-log",
                                    "LOG.${func.uppercase()} => [$tag] ${msg?.format(*args)}"
                                )
                            }
                        }
                    })
            }
        }
    }

    // Hook FileInputStream / FileOutputStream to trace file operations.
    private val traceFilesHooker = Hooker {
        if (pref.getBoolean(DEVELOPER_TRACE_FILES, false)) {
            findAndHookConstructor(C.FileInputStream, C.File, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val path = (param.args[0] as File?)?.absolutePath ?: return
                    Log.d("Xposed", "FILE => Read $path")
                }
            })

            findAndHookConstructor(C.FileOutputStream, C.File, C.Boolean, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val path = (param.args[0] as File?)?.absolutePath ?: return
                    Log.d("Xposed", "FILE => Write $path")
                }
            })

            findAndHookMethod(C.File, "delete", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val file = param.thisObject as File
                    Log.d("Xposed", "FILE => Delete ${file.absolutePath}")
                }
            })
        }
    }

    // Hook XML Parser to trace the XML files used in Wechat.
    private val traceXMLParseHooker = Hooker {
        if (pref.getBoolean(DEVELOPER_XML_PARSER, false)) {
            hookMethod(XmlParser_parse, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val xml = param.args[0] as String?
                    val root = param.args[1] as String?
                    Log.d("Xposed", "XML => root = $root, xml = $xml")
                }
            })
        }
    }
}