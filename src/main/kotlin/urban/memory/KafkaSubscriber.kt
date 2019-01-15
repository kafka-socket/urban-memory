package urban.memory

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread

class KafkaSubscriber {
    private val config = Config.kafka
    private val consumerTopic = config.getString("consumer.topic")
    private val consumer = initConsumer()
    private val logger: Logger = LoggerFactory.getLogger(KafkaSubscriber::class.java)

    fun run() {
        logger.info("Run consumer")
        thread(name = "kafka consumer") {
            while (true) {
                consumer.poll(Duration.ofMillis(100)).forEach { handleRecord(it) }
            }
        }
    }

    private fun initConsumer() : KafkaConsumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = config.getString("bootstrap.servers")
        props["group.id"] = "urban-memory"
        props["enable.auto.commit"] = "true"
        props["auto.commit.interval.ms"] = 1_000
        props["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
        props["value.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"

        return KafkaConsumer<String, String>(props).also {
            it.subscribe(Arrays.asList(consumerTopic))
        }
    }

    private fun handleRecord(record: ConsumerRecord<String, String>) {
        val user = record.key()
        val message = record.value()
        logger.info("Received [$message] for [$user]")
        Channels.byKey(user)?.forEach {
            logger.info("Sending [$message] via ws-channel $it")
            it.send(message)
        }
    }
}
