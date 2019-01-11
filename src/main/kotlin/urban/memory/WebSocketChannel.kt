package urban.memory

import org.apache.kafka.common.header.Header
import org.apache.kafka.common.protocol.types.Field
import org.eclipse.jetty.websocket.api.RemoteEndpoint
import org.eclipse.jetty.websocket.api.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WebSocketChannel(
    override val key: String,
    session: Session,
    private val remote: RemoteEndpoint
) : Channel {
    private val logger: Logger = LoggerFactory.getLogger(WebSocketChannel::class.java)

    init {
        session.upgradeRequest.getHeader("user-agent").also {
            logger.info("User [$key] with User-Agent [$it] connected")
        }
    }

    override fun send(message: String) {
        remote.sendString(message)
    }

    fun onInit() {
        logger.info("onInit")
        KafkaClient.send(key, headers = headers("init"))
    }

    fun onClose() {
        logger.info("onClose")
        KafkaClient.send(key, headers = headers("terminate"))
    }

    fun onText(message: String) {
        KafkaClient.send(key, message, headers("text"))
    }

    private fun headers(type: String) : MutableIterable<Header> {
        return mutableListOf<Header>(KafkaHeader("type", type))
    }
}
