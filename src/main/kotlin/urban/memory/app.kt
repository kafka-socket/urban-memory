package urban.memory

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Spark.*

fun main(args: Array<String>) {
    val logger: Logger = LoggerFactory.getLogger("app")

    webSocket("/ws", WebSocketHandler::class.java)
    before("/ws") { request, response ->
        val token = request.queryParams("token")
        logger.info("token received [$token]")
        when {
            token.isNullOrBlank() -> halt(401, "Token required")
            !Auth(token).isOk() -> halt(403, "Not allowed")
            else -> logger.info("Token is valid")
        }

    }
    init()
}
