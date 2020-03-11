package cn.vove7


import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor
import java.io.StringBufferInputStream
import javax.management.modelmbean.XMLParseException
import javax.sql.rowset.spi.XmlReader
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.stream.XMLEventReader


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.jitpack(testing: Boolean = false) = routing {

    route("jitpack") {

        get("{user}/{repo}.svg") {
            processFindJitpack {
                call.respondText(ContentType.Image.SVG) {
                    """
                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="48" height="20">
                        <linearGradient id="smooth" x2="0" y2="100%">
                            <stop offset="0" stop-color="#bbb" stop-opacity=".1"></stop>
                            <stop offset="1" stop-opacity=".1"></stop>
                        </linearGradient>
                        <mask id="round">
                            <rect width="48" height="20" rx="3" fill="#fff"></rect>
                        </mask>
                        <g mask="url(#round)">
                            <rect width="48" height="20" fill="#4c1" style="
                        /* color: #3fff1d; */
                    "></rect>
                            <rect width="48" height="20" fill="url(#smooth)"></rect>
                        </g>

                        <g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11">
                            <text x="25" y="14">$it</text>
                        </g>
                    </svg>
                """.trimIndent()
                }
            }
        }
        get("{user}/{repo}.raw") {
            processFindJitpack {
                call.respondText { it }
            }
        }
        //优先级低
        get("{user}/{repo}") {
            processFindJitpack {
                call.respondText(ContentType.Image.SVG) {
                    """
                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" height="20">
                      <g fill="#4c1" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11">
                        <text x="20" y="14"> $it</text>
                      </g>
                    </svg>
                    """.trimIndent()
                }
            }
        }
    }
}

private suspend fun PipelineContext<*, ApplicationCall>.processFindJitpack(onSuccess: suspend (String) -> Unit) {
    val user = call.parameters["user"]
    val repo = call.parameters["repo"]
    if (user.isNullOrBlank() || repo.isNullOrBlank()) {
        call.respond(HttpStatusCode.BadRequest, "$user/$repo")
        return@processFindJitpack
    }
    val s = try {
        getLatestReleaseVersion(user, repo)
    } catch (e: Exception) {
        call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
        return@processFindJitpack
    }
    if (s == null) {
        call.respond(HttpStatusCode.NotFound, "NotFound $user/$repo on Jitpack")
    } else {
        onSuccess(s)
    }
}

suspend fun getLatestReleaseVersion(user: String, repo: String): String? {
    val client = HttpClient()
    val s = client.get<String>("https://jitpack.io/v/$user/$repo.svg")

    val fact = DocumentBuilderFactory.newInstance();
    val n = fact.newDocumentBuilder().parse(StringBufferInputStream(s))
    val gs = n.getElementsByTagName("g")
    return try {
        gs.item(1).childNodes.item(5).textContent.trim()
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}