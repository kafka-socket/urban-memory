package urban.memory

import kotlinx.coroutines.Job
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.Headers

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import org.eclipse.jetty.websocket.api.WebSocketPingPongListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class WebSocketHandler : WebSocketAdapter(), WebSocketPingPongListener {
    private var channel: WebSocketChannel? = null
    private var heartbeatJob: Job? = null
    private val heartbeatIntervalMillis: Long = Config.heartbeatIntervalMillis
    private val logger: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)

    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        val token = session!!.upgradeRequest.parameterMap["token"]!!.first()
        logger.info("Token is [$token]")
        val user = Token(token).user()!!
        logger.info("User [$user] authenticated")
        WebSocketChannel(user, session, remote).also {
            Channels.add(it)
            channel = it
            it.onInit()
        }
        heartbeatJob = heartbeat()
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)
        logger.info("Connection closed with code $statusCode:[$reason]")
        channel?.onClose()
        if (channel != null) {
            Channels.remove(channel!!)
        }
        heartbeatJob?.cancel()
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {
        logger.info("Binary received: [$payload], [$offset], [$len]")
    }

    override fun onWebSocketText(message: String?) {
        logger.debug("Message received [$message]")
        channel?.onText(message.orEmpty())
    }

    override fun onWebSocketPing(payload: ByteBuffer?) {
        logger.info("Ping received: [$payload]")
        remote.sendPong(payload)
    }

    override fun onWebSocketPong(payload: ByteBuffer?) {
        logger.debug("Pong received: [${StandardCharsets.UTF_8.decode(payload)}]")
    }

    override fun onWebSocketError(cause: Throwable?) {
        logger.error(cause.toString())
    }

    private fun heartbeat() : Job {
        return GlobalScope.launch {
            while (isActive) {
                remote.sendPing(ByteBuffer.wrap("beat".toByteArray()))
                delay(heartbeatIntervalMillis)
            }
        }
    }
}
