package urban.memory

fun main(args: Array<String>) {
    KafkaSubscriber().run()
    Web().start()
}
