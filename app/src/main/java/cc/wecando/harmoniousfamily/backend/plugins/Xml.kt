package cc.wecando.harmoniousfamily.backend.plugins

import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IXmlParserHook

object Xml : IXmlParserHook {
    override fun onXmlParsed(
        xml: String,
        root: String,
        newParam: String?,
        result: MutableMap<String, String>
    ) {
        Log.d("Xposed-onXmlParsed", "xml:${xml},root:${root},${newParam}")
        super.onXmlParsed(xml, root, newParam, result)
    }
}