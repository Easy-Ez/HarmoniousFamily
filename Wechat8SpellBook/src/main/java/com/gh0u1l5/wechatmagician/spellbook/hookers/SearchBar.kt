package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.base.Versions
import com.gh0u1l5.wechatmagician.spellbook.interfaces.ISearchBarConsole
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.tools.Classes
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.tools.Classes.ActionBarEditText
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.tools.Classes.ActionBarSearchView
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.tools.Fields
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers

object SearchBar : EventCenter() {
    private val mainHandler = Handler(Looper.getMainLooper())
    override val interfaces: List<Class<*>>
        get() = listOf(ISearchBarConsole::class.java)

    override fun provideEventHooker(event: String): Hooker {
        return when (event) {
            "onHandleCommand" -> SearchBarHooker
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    class CommandTextWatcher(private val search: EditText) : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?, start: Int, count: Int, after: Int
        ) = Unit

        override fun onTextChanged(
            s: CharSequence?, start: Int, before: Int, count: Int
        ) = Unit

        override fun afterTextChanged(editable: Editable?) {
            editable?.let {
                val context = search.context
                var command = it.toString()
                Log.d("Xposed", "ActionBarEditText textChange:${command}")
                if (command.startsWith("#") && command.endsWith("#")) {
                    command = command.drop(1).dropLast(1)
                    notifyParallelForResults("onHandleCommand", { plugin ->
                        (plugin as ISearchBarConsole).onHandleCommand(
                            context, command
                        )
                    }) { results ->
                        val consumed = results.any { result -> result }
                        if (consumed) {
                            mainHandler.post {
                                // clear
                                it.clear()
                                // Hide Input Method
                                val imm = search.context.getSystemService(INPUT_METHOD_SERVICE)
                                (imm as InputMethodManager).hideSoftInputFromWindow(
                                    search.windowToken, 0
                                )
                            }

                        }
                    }
                }
            }

        }
    }

    private val SearchBarHooker = Hooker {
        if (WechatGlobal.wxVersion!! >= Versions.v8_0_30) {
            XposedHelpers.findAndHookMethod(ActionBarSearchView, "init", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val search = Fields.ActionBarSearchView_mEditText.get(param.thisObject) as EditText
                    search.addTextChangedListener(CommandTextWatcher(search))
                }
            })
        } else {
            hookAllConstructors(ActionBarEditText, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    Log.d("Xposed", "ActionBarEditText Constructors:${param.thisObject}")
                    val search = param.thisObject as EditText
                    search.addTextChangedListener(CommandTextWatcher(search))
                }
            })

        }
        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_COMMAND)
    }
}
