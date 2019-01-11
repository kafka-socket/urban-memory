package urban.memory

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread

object KafkaClient {
    private val config = Config.kafka
    private val producerTopic = config.getString("producer.topic")
    private val producer = initProducer()

    fun send(key: String = "", value: String = "", headers: MutableIterable<Header> = mutableListOf()) {
        producer.send(ProducerRecord(producerTopic, null, key, value, headers))
    }

    private fun initProducer() : KafkaProducer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = config.getString("bootstrap.servers")
        props["acks"] = "all"
        props["delivery.timeout.ms"] = 30_000
        props["batch.size"] = 16_384
        props["linger.ms"] = 1
        props["request.timeout.ms"] = 20_000
        props["buffer.memory"] = 33554432
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"

        return KafkaProducer(props)
    }
}
