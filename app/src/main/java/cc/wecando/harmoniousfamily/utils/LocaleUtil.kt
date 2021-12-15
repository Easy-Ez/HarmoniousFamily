package cc.wecando.harmoniousfamily.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import cc.wecando.harmoniousfamily.Global.PREFERENCE_NAME_SETTINGS
import cc.wecando.harmoniousfamily.Global.SETTINGS_MODULE_LANGUAGE
import java.util.*

/**
 * Created by devdeeds.com on 18/4/17.
 * by Jayakrishnan P.M
 */

object LocaleUtil {

    fun getLanguage(context: Context, default: String = Locale.getDefault().language): String {
        val settings = context.getSharedPreferences(PREFERENCE_NAME_SETTINGS, MODE_PRIVATE)
        return settings.getString(SETTINGS_MODULE_LANGUAGE, default)!!
    }

    fun onAttach(context: Context, default: String = Locale.getDefault().language): Context {
        val language = getLanguage(context, default)
        return setLocale(context, language)
    }

    fun setLocale(context: Context, language: String): Context {
        persist(context, language)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            updateResourcesLegacy(context, language)
        }

    }

    private fun persist(context: Context, language: String) {
        val settings = context.getSharedPreferences(PREFERENCE_NAME_SETTINGS, MODE_PRIVATE)
        settings.edit()
            .putString(SETTINGS_MODULE_LANGUAGE, language)
            .apply()
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        val displayMetrics = resources.displayMetrics
        resources.configuration.locale = locale
        resources.updateConfiguration(configuration, displayMetrics)

        return context
    }
}