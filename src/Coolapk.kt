package cn.vove7

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import org.jsoup.Jsoup

/**
 * 酷安
 *
 * 接口抓包失败
 *
 * @receiver Application
 * @param testing Boolean
 * @return Routing
 */
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.coolapk(testing: Boolean = false) = routing {
    route("coolapk") {
        get("{pkg}") {
            val pkg = call.parameters["pkg"]
            if (pkg == null) {
                call.badRequest()
            } else {
                call.respond(getSimpleInfo(pkg) ?: "")
            }
        }
    }

}

fun getSimpleInfo(pkg: String): List<String>? = runCatching {
    Jsoup.connect("https://www.coolapk.com/apk/$pkg").get()
}.let {
    if (it.isSuccess) {
        val doc = it.getOrNull() ?: return@let null

        val list = mutableListOf<String>()
        val titleEle = doc.getElementsByClass("detail_app_title")
        val version = titleEle[0].getElementsByClass("list_app_info")?.text()?.trim()

        var title = titleEle?.text()
        title = title?.removeSuffix(version ?: "")?.trim()
        list += title ?: ""
        list += version ?: ""
        val t = doc.getElementsByClass("apk_topba_message")?.text()

        list.addAll(t?.split('/')?.map { it.trim() }?.toList() ?: emptyList())
        list
    } else {
        null
    }
}
