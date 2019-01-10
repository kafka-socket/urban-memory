package urban.memory

import org.apache.kafka.common.header.Header

data class KafkaHeader(private val key: String, private val value: String) : Header {
    override fun key(): String = key
    override fun value(): ByteArray = value.toByteArray()
}
