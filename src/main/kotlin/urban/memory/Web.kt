package urban.memory

import org.eclipse.jetty.http.HttpStatus.FORBIDDEN_403
import org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response

import spark.Spark.webSocket
import spark.Spark.before
import spark.Spark.init
import spark.Spark.halt
import spark.Spark.port

class Web(private val config: Config) {
    private val logger: Logger = LoggerFactory.getLogger(Web::class.java)

    fun start() {
        port(config.port)
        webSocket("/ws", WebSocketHandler::class.java)
        before("/ws", this::auth)
        init()
    }

    private fun auth(request: Request, response: Response) {
        val token = request.queryParams("token")
        logger.info("token received [$token]")
        when {
            token.isNullOrBlank() -> halt(UNAUTHORIZED_401, "Token required")
            !Token(config, token).isOk() -> halt(FORBIDDEN_403, "Not allowed")
            else -> logger.info("Token is valid")
        }
    }
}
