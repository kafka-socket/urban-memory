package urban.memory

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WebSocketHandler : WebSocketListener {
    private var session: Session? = null
    private val logger: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)

    override fun onWebSocketConnect(s: Session?) {
        session = s
        logger.info("Incoming connection with [${userAgent()}]")
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {
        logger.info("Binary received: [$payload], [$offset], [$len]")
    }

    override fun onWebSocketText(message: String?) {
        logger.info("Message received [$message] from session [${userAgent()}]")
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        logger.info("Connection closed with code $statusCode:[$reason]")
    }

    override fun onWebSocketError(cause: Throwable?) {
        TODO("not implemented")
    }

    private fun userAgent() : String {
        return session?.upgradeRequest?.getHeader("user-agent") ?: "Unknown"
    }
}
