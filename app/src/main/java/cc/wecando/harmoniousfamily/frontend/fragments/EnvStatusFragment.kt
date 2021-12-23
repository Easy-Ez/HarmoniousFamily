package cc.wecando.harmoniousfamily.frontend.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cc.wecando.harmoniousfamily.Global.ACTION_REQUIRE_HOOK_STATUS
import cc.wecando.harmoniousfamily.R
import cc.wecando.harmoniousfamily.databinding.FragmentStatusBinding
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus

class EnvStatusFragment : Fragment() {
    private lateinit var bind: FragmentStatusBinding
    private val componentMap = mapOf(
        WechatStatus.StatusFlag.STATUS_FLAG_MSG_STORAGE to R.id.component_msg_storage_status,
        WechatStatus.StatusFlag.STATUS_FLAG_RESOURCES to R.id.component_resources_status,
        WechatStatus.StatusFlag.STATUS_FLAG_DATABASE to R.id.component_database_status,
        WechatStatus.StatusFlag.STATUS_FLAG_XML_PARSER to R.id.component_xml_parser_status,
        WechatStatus.StatusFlag.STATUS_FLAG_URI_ROUTER to R.id.component_uri_router_status,
        WechatStatus.StatusFlag.STATUS_FLAG_GAME to R.id.component_game_status
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentStatusBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onStart() {
        super.onStart()
        if (isModuleLoaded()) {
            // Set the main banner of status fragment.
            val color: Int
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || getXposedVersion() >= 89) {
                color = ContextCompat.getColor(requireActivity(), R.color.ok)
                bind.statusText.text = getString(R.string.status_ok)
                bind.statusImage.setImageResource(R.drawable.ic_status_ok)
                bind.statusImage.contentDescription = getString(R.string.status_ok)
            } else {
                color = ContextCompat.getColor(requireActivity(), R.color.warn)
                bind.statusText.text = getString(R.string.status_outdated_xposed)
                bind.statusImage.setImageResource(R.drawable.ic_status_error)
                bind.statusImage.contentDescription = getString(R.string.status_outdated_xposed)
            }
            bind.statusText.setTextColor(color)
            bind.statusImage.setBackgroundColor(color)

            // Set the status for each component.
            requireHookStatus(requireActivity()) { status ->
                for (entry in componentMap) {
                    if (status.contains(entry.key)) {
                        setComponentIconValid(entry.value)
                    }
                }
            }
        }
    }

    private fun setComponentIconValid(iconId: Int) {
        val icon = bind.root.findViewById<ImageView>(iconId)
        if (icon != null) {
            icon.setImageResource(R.drawable.ic_component_valid)
            icon.contentDescription = getString(R.string.status_component_valid)
        }
    }

    // Check backend.WechatHook for actual implementation
    private fun isModuleLoaded(): Boolean {
        // In some frameworks, short methods (less than two Dalvik instructions)
        // can not be hooked stably. This log just makes the method longer to hook.
        Log.v(TAG, "$javaClass.isModuleLoaded() invoked.")
        return false
    }

    // Check backend.WechatHook for actual implementation
    private fun getXposedVersion(): Int {
        // In some frameworks, short methods (less than two Dalvik instructions)
        // can not be hooked stably. This log just makes the method longer to hook.
        Log.v(TAG, "$javaClass.getXposedVersion() invoked. ")
        return 0
    }

    companion object {
        private const val TAG = "StatusFragment"

        fun newInstance(): EnvStatusFragment = EnvStatusFragment()

        fun requireHookStatus(context: Context, callback: (List<WechatStatus.StatusFlag>) -> Unit) {
            val intent =
                Intent(ACTION_REQUIRE_HOOK_STATUS).addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            context.sendOrderedBroadcast(intent, null, object : BroadcastReceiver() {
                @Suppress("UNCHECKED_CAST")
                override fun onReceive(context: Context?, intent: Intent?) {
                    val result = getResultExtras(true)
                    val status = result.getIntArray("status")
                    if (status != null) {
                        val flags = WechatStatus.StatusFlag.values()
                        callback(status.map { flags[it] })
                    }
                }
            }, null, Activity.RESULT_OK, null, null)
        }
    }
}