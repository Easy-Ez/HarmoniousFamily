package cc.wecando.harmoniousfamily.frontend.fragments

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import cc.wecando.harmoniousfamily.Global.ACTION_UPDATE_PREF
import cc.wecando.harmoniousfamily.Global.MAGICIAN_PACKAGE_NAME
import cc.wecando.harmoniousfamily.Global.SETTINGS_GAME_DICE
import cc.wecando.harmoniousfamily.Global.SETTINGS_GAME_DICE_FLAG
import cc.wecando.harmoniousfamily.Global.SETTINGS_GAME_RPS
import cc.wecando.harmoniousfamily.Global.SETTINGS_GAME_RPS_FLAG
import cc.wecando.harmoniousfamily.Global.SETTINGS_INTERFACE_HIDE_ICON
import cc.wecando.harmoniousfamily.Global.SETTINGS_MODULE_LANGUAGE
import cc.wecando.harmoniousfamily.R
import cc.wecando.harmoniousfamily.utils.GameUtils
import cc.wecando.harmoniousfamily.utils.IPCUtil.putExtra
import cc.wecando.harmoniousfamily.utils.LocaleUtil

class PrefFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val gameKeyArray = arrayOf(
        SETTINGS_GAME_RPS, SETTINGS_GAME_DICE, SETTINGS_GAME_RPS_FLAG, SETTINGS_GAME_DICE_FLAG
    )

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        if (arguments != null) {
            val preferencesResId = requireArguments().getInt(ARG_PREF_RES)
            val preferencesName = requireArguments().getString(ARG_PREF_NAME)
            preferenceManager.sharedPreferencesName = preferencesName
            addPreferencesFromResource(preferencesResId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.card_background))
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeGameTitle()
    }

    override fun onStart() {
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        super.onStart()
    }

    override fun onStop() {
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        tryChangeGameTitleWhenSpChange(key)
        when (key) {
            SETTINGS_INTERFACE_HIDE_ICON -> {
                // Hide/Show the icon as required.
                try {
                    val hide = sharedPreferences.getBoolean(SETTINGS_INTERFACE_HIDE_ICON, false)
                    val newState =
                        if (hide) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    val className = "$MAGICIAN_PACKAGE_NAME.frontend.MainActivityAlias"
                    val componentName = ComponentName(MAGICIAN_PACKAGE_NAME, className)
                    requireActivity().packageManager.setComponentEnabledSetting(
                        componentName, newState, PackageManager.DONT_KILL_APP
                    )
                } catch (t: Throwable) {
                    Log.e(TAG, "Cannot hide icon: $t")
                    Toast.makeText(activity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
            SETTINGS_MODULE_LANGUAGE -> {
                try {
                    val language = LocaleUtil.getLanguage(requireActivity())
                    LocaleUtil.setLocale(requireActivity(), language)
                    requireActivity().recreate()
                } catch (t: Throwable) {
                    Log.e(TAG, "Cannot change language: $t")
                    Toast.makeText(activity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                val value = sharedPreferences.all[key] ?: return
                activity?.sendBroadcast(Intent(ACTION_UPDATE_PREF).apply {
                    putExtra("key", key)
                    putExtra("value", value)
                })
            }
        }
    }

    /**
     * sp 变化时, 尝试更新相关 UI
     */
    private fun tryChangeGameTitleWhenSpChange(key: String) {
        if (key in gameKeyArray) {
            changeGameTitle()
        }
    }

    /**
     * 根据 game 存放在 sp 中的值, 更新 UI
     */
    private fun changeGameTitle() {
        val sp = preferenceScreen.sharedPreferences
        val rpsPref = findPreference(SETTINGS_GAME_RPS) as Preference?
        val dicePref = findPreference(SETTINGS_GAME_DICE) as Preference?

        rpsPref?.let {
            it.title = if (sp?.getBoolean(SETTINGS_GAME_RPS_FLAG, false) == true) {
                getString(
                    R.string.title_rps_list, GameUtils.getRPSTextByValue(
                        sp.getInt(
                            SETTINGS_GAME_RPS, 0
                        ), requireContext()
                    )
                )
            } else {
                getString(
                    R.string.title_rps_list_disable
                )
            }
        }
        dicePref?.let {
            it.title = if (sp?.getBoolean(SETTINGS_GAME_DICE_FLAG, false) == true) {
                getString(
                    R.string.title_dice_list, sp.getInt(
                        SETTINGS_GAME_DICE, 0
                    ) + 1
                )
            } else {
                getString(
                    R.string.title_dice_list_disable
                )
            }
        }

    }

    companion object {
        private const val TAG = "PrefFragment"

        private const val ARG_PREF_RES = "preferencesResId"
        private const val ARG_PREF_NAME = "preferencesFileName"

        fun newInstance(preferencesResId: Int, preferencesName: String): PrefFragment {
            val fragment = PrefFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_PREF_RES, preferencesResId)
                putString(ARG_PREF_NAME, preferencesName)
            }
            return fragment
        }
    }

}


