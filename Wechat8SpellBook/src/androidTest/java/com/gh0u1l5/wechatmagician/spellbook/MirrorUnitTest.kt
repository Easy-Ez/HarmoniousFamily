package com.gh0u1l5.wechatmagician.spellbook

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorClasses
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorFields
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorMethods
import com.gh0u1l5.wechatmagician.spellbook.parser.ApkFile
import com.gh0u1l5.wechatmagician.spellbook.util.FileUtil
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import dalvik.system.PathClassLoader
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.lang.ClassLoader.getSystemClassLoader
import kotlin.system.measureTimeMillis

/**
 * 自动化的微信版本适配测试
 */
@ExperimentalUnsignedTypes
class MirrorUnitTest {
    companion object {
        private const val DOMESTIC_DIR = "apks/domestic"
        private const val PLAY_STORE_DIR = "apks/play-store"
    }

    private var context: Context? = null

    @Before
    fun initialize() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun verifyPackage(apkPath: String) {
        // 解析 APK 版本
        val regex = Regex("wechat-v(.*)\\.apk")
        val match = regex.find(apkPath) ?: throw Exception("Unexpected path format")
        val version = match.groupValues[1]
        // 将 APK 文件保存至 Cache 目录
        val cacheDir = context!!.cacheDir
        val apkFile = File(cacheDir, apkPath)
        try {
            javaClass.classLoader!!.getResourceAsStream(apkPath).use {
                FileUtil.writeInputStreamToDisk(apkFile.absolutePath, it)
            }
        } catch (t: Throwable) {
            Log.w("MirrorUnitTest", t)
            return // ignore if the apk isn't accessible
        }

        // 确保 APK 文件存在 并开始自动化适配测试
        assertTrue(apkFile.exists())
        ApkFile(apkFile).use {
            // 测试 APK Parser 的解析速度
            val timeParseDex = measureTimeMillis { it.classTypes }
            Log.d("MirrorUnitTest", "Benchmark: Parsing APK takes $timeParseDex ms.")

            // 初始化 WechatGlobal
            WechatGlobal.wxUnitTestMode = true
            WechatGlobal.wxVersion = Version(version)
            WechatGlobal.wxPackageName = "com.tencent.mm"
            WechatGlobal.wxLoader = PathClassLoader(apkFile.absolutePath, getSystemClassLoader())
            WechatGlobal.wxClasses = it.classTypes

            // 清理上次测试留下的缓存
            val objects = MirrorClasses + MirrorMethods + MirrorFields
            ReflectionUtil.clearClassCache()
            ReflectionUtil.clearMethodCache()
            objects.forEach { instance ->
                MirrorUtil.clearUnitTestLazyFields(instance)
            }
            Log.d("MirrorUnitTest", "version: ${WechatGlobal.wxVersion}.")
            // 进行适配测试并生成结果
            var result: List<Pair<String, String>>?
            val timeSearch = measureTimeMillis {
                result = MirrorUtil.generateReportWithForceEval(objects)
            }
            Log.d("MirrorUnitTest", "Benchmark: Searching over classes takes $timeSearch ms.")
            result?.forEach { entry ->
                Log.d("MirrorUnitTest", "Verified: ${entry.first} -> ${entry.second}")
            }
        }

        apkFile.delete()
    }

    @Test
    fun verifyDomesticPackage8_0_30() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.30.apk")
    }
    @Test
    fun verifyDomesticPackage8_0_23() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.23.apk")
    }

    @Test
    fun verifyDomesticPackage8_0_16() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.16.apk")
    }

    @Test
    fun verifyDomesticPackage8_0_11() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.11.apk")
    }

    @Test
    fun verifyDomesticPackage8_0_10() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.10.apk")
    }

    @Test
    fun verifyDomesticPackage8_0_9() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.9.apk")
    }


    @Test
    fun verifyDomesticPackage8_0_7() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.7.apk")
    }

    @Test
    fun verifyDomesticPackage8_0_6() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.6.apk")
    }

    @Test
    fun verifyDomesticPackage8_0_1() {
        verifyPackage("$DOMESTIC_DIR/wechat-v8.0.1.apk")
    }

//    @Test fun verifyDomesticPackage6_6_0() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.0.apk")
//    }
//
//    @Test fun verifyDomesticPackage6_6_1() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.1.apk")
//    }
//
//    @Test fun verifyDomesticPackage6_6_2() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.2.apk")
//    }
//
//    @Test fun verifyDomesticPackage6_6_3() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.3.apk")
//    }
//
//    @Test fun verifyDomesticPackage6_6_5() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.5.apk")
//    }
//
//    @Test fun verifyDomesticPackage6_6_6() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.6.apk")
//    }
//
//    @Test fun verifyDomesticPackage6_6_7() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.7.apk")
//    }
//
//    @Test fun verifyDomesticPackage6_7_2() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.7.2.apk")
//    }
//
//    @Test fun verifyDomesticPackage6_7_3() {
//        verifyPackage("$DOMESTIC_DIR/wechat-v6.7.3.apk")
//    }
//
//    @Test fun verifyPlayStorePackage6_6_1() {
//        verifyPackage("$PLAY_STORE_DIR/wechat-v6.6.1.apk")
//    }
//
//    @Test fun verifyPlayStorePackage6_6_2() {
//        verifyPackage("$PLAY_STORE_DIR/wechat-v6.6.2.apk")
//    }
//
//    @Test fun verifyPlayStorePackage6_6_6() {
//        verifyPackage("$PLAY_STORE_DIR/wechat-v6.6.6.apk")
//    }
//
//    @Test fun verifyPlayStorePackage6_6_7() {
//        verifyPackage("$PLAY_STORE_DIR/wechat-v6.6.7.apk")
//    }
//
//    @Test fun verifyPlayStorePackage6_7_3() {
//        verifyPackage("$PLAY_STORE_DIR/wechat-v6.7.3.apk")
//    }
}