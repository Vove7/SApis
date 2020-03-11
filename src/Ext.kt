package cn.vove7

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

@Suppress("NOTHING_TO_INLINE")
suspend inline fun ApplicationCall.notFound(message: Any? = null) {
    response.status(HttpStatusCode.NotFound)
    if (message != null) {
        response.pipeline.execute(this, message)
    }
}
@Suppress("NOTHING_TO_INLINE")
suspend inline fun ApplicationCall.badRequest(message: Any? = null) {
    response.status(HttpStatusCode.BadRequest)
    if (message != null) {
        response.pipeline.execute(this, message)
    }
}
