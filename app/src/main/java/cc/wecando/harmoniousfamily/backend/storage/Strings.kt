package cc.wecando.harmoniousfamily.backend.storage

import android.util.Log
import cc.wecando.harmoniousfamily.Global.SETTINGS_MODULE_LANGUAGE
import cc.wecando.harmoniousfamily.R
import cc.wecando.harmoniousfamily.backend.WechatHook
import java.util.*

object Strings {

    private val pref = WechatHook.settings

    @Volatile
    var language: String = Locale.getDefault().language

    // Use hard coded strings if the module cannot load resources from its APK
    private val hardcodedStrings: Map<String, Map<Int, String>> = mapOf(
        // NOTE: Replace <string name="(.*?)">(.*?)</string> to R.string.$1 to "$2",
        "en" to mapOf(
            R.string.button_ok to "Okay",
            R.string.button_update to "Update",
            R.string.button_cancel to "Cancel",
            R.string.button_clean_unread to "Mark All as Read",
            R.string.button_hide_chatroom to "Hide Useless Chatroom",
            R.string.button_unhide_chatroom to "Unhide Chatroom",
            R.string.button_hide_friend to "Hide Friend",
            R.string.button_select_all to "All",
            R.string.button_sns_forward to "Forward",
            R.string.button_sns_screenshot to "Screenshot",

            R.string.prompt_alipay_not_found to "Alipay Not Found",
            R.string.prompt_load_component_status_failed to "Failed to retrieve the status of components.",
            R.string.prompt_message_recall to "want to recall the message, idiot.",
            R.string.prompt_need_reboot to "Take effect after reboot.",
            R.string.prompt_screenshot to "The screenshot has been saved to",
            R.string.prompt_sns_invalid to "Record is invalid or deleted.",
            R.string.prompt_update_discovered to "Update Discovered",
            R.string.prompt_wait to "Please wait for a while.....",

            R.string.prompt_setup_password to "Enter a new password",
            R.string.prompt_verify_password to "Enter your password",
            R.string.prompt_user_not_found to "User Not Found!",
            R.string.prompt_password_missing to "Please set your password first!",
            R.string.prompt_correct_password to "Correct Password!",
            R.string.prompt_wrong_password to "Wrong Password!",

            R.string.label_deleted to "[Deleted]",
            R.string.label_unnamed to "[Unnamed]",
            R.string.title_secret_friend to "Secret Friend"
        ),
        "zh" to mapOf(
            R.string.button_ok to "??????",
            R.string.button_update to "??????",
            R.string.button_cancel to "??????",
            R.string.button_clean_unread to "????????????????????????",
            R.string.button_hide_chatroom to "??????????????????",
            R.string.button_unhide_chatroom to "????????????",
            R.string.button_hide_friend to "????????????",
            R.string.button_select_all to "??????",
            R.string.button_sns_forward to "??????",
            R.string.button_sns_screenshot to "??????",

            R.string.prompt_alipay_not_found to "?????????????????????????????????",
            R.string.prompt_load_component_status_failed to "????????????????????????",
            R.string.prompt_message_recall to "?????????????????????????????????",
            R.string.prompt_need_reboot to "???????????????",
            R.string.prompt_screenshot to "??????????????????",
            R.string.prompt_sns_invalid to "????????????????????????",
            R.string.prompt_update_discovered to "????????????",
            R.string.prompt_wait to "?????????????????????",

            R.string.prompt_setup_password to "??????????????????",
            R.string.prompt_verify_password to "?????????????????????",
            R.string.prompt_user_not_found to "???????????????",
            R.string.prompt_password_missing to "??????????????????",
            R.string.prompt_correct_password to "????????????",
            R.string.prompt_wrong_password to "????????????",

            R.string.label_deleted to "[?????????]",
            R.string.label_unnamed to "[?????????]",
            R.string.title_secret_friend to "??????"
        )
    )

    fun getString(id: Int): String {
        val resources = WechatHook.resources
        if (resources != null) {
            return resources.getString(id)
        }
        val language = pref.getString(SETTINGS_MODULE_LANGUAGE, language)
        if (language !in hardcodedStrings) {
            Log.d("Xposed", "Unknown Language: $language")
        }
        val strings = hardcodedStrings[language] ?: hardcodedStrings["en"]
        if (id !in strings!!) {
            Log.d("Xposed", "Unknown String ID: $id")
        }
        return strings[id] ?: "???"
    }
}