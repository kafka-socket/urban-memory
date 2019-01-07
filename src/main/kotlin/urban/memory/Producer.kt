package urban.memory

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class Producer(config: Config) {
    private val config = config.producer
    private val producer = initProducer()

    fun send(topic: String, key: String, value: String) {
        producer.send(ProducerRecord(topic, key, value))
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
