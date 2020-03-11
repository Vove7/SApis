package cn.vove7

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.features.ResponseException
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import org.apache.http.HttpStatus


/**
 * JetBrain 插件相关api
 * @receiver Application
 * @param testing Boolean
 * @return Routing
 */
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.jetbrainPlugins(testing: Boolean = false) = routing {
    route("jetbrain/plugins") {
        /**
         * 插件下载数量
         */
        get("dc/{id}") {
            val id = call.parameters["id"]
            val style = call.request.queryParameters["style"]
            if (id == null) {
                call.respond(HttpStatusCode.BadGateway)
            } else {
                val dc = getPluginDownloadCount(id)
                if (dc == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    if (style != null) {
                        val s = style.replace("{dc}", dc.toString())
                        val svg = prettyWithShields(s)
                        if (svg == null) {
                            call.respond(HttpStatusCode.NotFound, "with style $style")
                        } else {
                            call.respondText(svg, ContentType.Image.SVG)
                        }
                    } else {
                        call.respondText(dc.toString())
                    }
                }
            }
        }
    }

}

suspend fun getPluginDownloadCount(id: String): Long? = httpClient.use {
    try {
        val m = it.get<Map<String, Any>>("https://plugins.jetbrains.com/api/plugins/$id")
        (m["downloads"] as Double).toLong()
    } catch (e: Exception) {
        null
    }
}

/**
 * 使用shields.io
 *
 * @param styleText String
 * @return String?
 */
suspend fun prettyWithShields(styleText: String): String? = httpClient.use {
    runCatching {
        it.get<String>("https://img.shields.io/$styleText")
    }.let {
        if (it.isSuccess) {
            it.getOrNull()
        } else null
    }
}