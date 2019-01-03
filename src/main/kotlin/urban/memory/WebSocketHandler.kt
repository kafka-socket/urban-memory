package urban.memory

import kotlinx.coroutines.Job
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import org.eclipse.jetty.websocket.api.WebSocketPingPongListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class WebSocketHandler : WebSocketAdapter(), WebSocketPingPongListener {
    private var heartbeatJob: Job? = null
    private val logger: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)
    private val config = Config()

    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        logger.info("Incoming connection with [${userAgent()}]")
        val token = session!!.upgradeRequest.parameterMap["token"]!!.first()
        logger.info("Token is [$token]")
        val user = Token(config, token).user()
        logger.info("User [$user] authenticated")
        heartbeatJob = heartbeat()
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)
        logger.info("Connection closed with code $statusCode:[$reason]")
        heartbeatJob?.cancel()
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {
        logger.info("Binary received: [$payload], [$offset], [$len]")
    }

    override fun onWebSocketText(message: String?) {
        logger.info("Message received [$message] from session [${userAgent()}]")
    }

    override fun onWebSocketPing(payload: ByteBuffer?) {
        logger.info("Ping received: [$payload]")
        remote.sendPong(payload)
    }

    override fun onWebSocketPong(payload: ByteBuffer?) {
        logger.info("Pong received: [${StandardCharsets.UTF_8.decode(payload)}]")
    }

    override fun onWebSocketError(cause: Throwable?) {
        logger.error(cause.toString())
    }

    private fun userAgent() : String {
        return session?.upgradeRequest?.getHeader("user-agent") ?: "Unknown"
    }

    private fun heartbeat() : Job {
        return GlobalScope.launch {
            while (isActive) {
                remote.sendPing(ByteBuffer.wrap("beat".toByteArray()))
                delay(config.heartbeatIntervalMillis)
            }
        }
    }
}
