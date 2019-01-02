package urban.memory

import kotlinx.coroutines.*
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketListener
import org.eclipse.jetty.websocket.api.WebSocketPingPongListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class WebSocketHandler : WebSocketListener, WebSocketPingPongListener {
    private var session: Session? = null
    private var heartbeatJob: Job? = null
    private val logger: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)

    companion object {
        private const val HEARTBEAT_INTERVAL = 5_000L
    }

    override fun onWebSocketConnect(s: Session?) {
        session = s
        logger.info("Incoming connection with [${userAgent()}]")
        heartbeatJob = heartbeat()
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {
        logger.info("Binary received: [$payload], [$offset], [$len]")
    }

    override fun onWebSocketText(message: String?) {
        logger.info("Message received [$message] from session [${userAgent()}]")
    }

    override fun onWebSocketPing(payload: ByteBuffer?) {
        logger.info("Ping received: [$payload]")
        session?.remote?.sendPong(payload)
    }

    override fun onWebSocketPong(payload: ByteBuffer?) {
        logger.info("Pong received: [${StandardCharsets.UTF_8.decode(payload)}]")
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        logger.info("Connection closed with code $statusCode:[$reason]")
        heartbeatJob?.cancel()
    }

    override fun onWebSocketError(cause: Throwable?) {
        TODO("not implemented")
    }

    private fun userAgent() : String {
        return session?.upgradeRequest?.getHeader("user-agent") ?: "Unknown"
    }

    private fun heartbeat() : Job {
        return GlobalScope.launch {
            while (isActive) {
                session?.remote?.sendPing(ByteBuffer.wrap("beat".toByteArray()))
                delay(HEARTBEAT_INTERVAL)
            }
        }
    }
}
