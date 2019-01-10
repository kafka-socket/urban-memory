package urban.memory

fun main(args: Array<String>) {
    KafkaClient.runConsumer()
    Web().start()
}
