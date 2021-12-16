package cc.wecando.harmoniousfamily.frontend.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        WechatStatus.StatusFlag.STATUS_FLAG_URI_ROUTER to R.id.component_uri_router_status
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentStatusBinding.inflate(inflater, container, false)
        return bind.root
    }

    fun isModuleLoaded() = false
    fun getXposedVersion() = 0

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