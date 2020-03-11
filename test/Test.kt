import cn.vove7.getLatestReleaseVersion
import cn.vove7.getSimpleInfo
import cn.vove7.httpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import kotlin.test.Test

class Test {

    @Test
    fun jitpack() = runBlocking {
        val s = getLatestReleaseVersion("Vove7", "SlidePickerView")
        print(s)
        delay(100)
    }

    @Test
    fun pluginDc() = runBlocking {
        httpClient.use {
            val m = it.get<Map<String, Any>>("https://plugins.jetbrains.com/api/plugins/13075")
            println(m["downloads"] as Double)
        }
    }

    @Test
    fun prettyWithShields() = runBlocking {
        httpClient.use {
            val svg = it.get<String>("https://img.shields.io/badge/Jetbrains_Plugin-111}+-yellow.svg?style=social")
            print(svg)

        }
    }

    @Test
    fun coolapkAppInfo() = runBlocking {
        println(getSimpleInfo("com.eg.android.AlipayGphone"))
        println(getSimpleInfo("tk.louisstudio.daily_notes"))
    }
}
