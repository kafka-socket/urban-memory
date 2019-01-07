package urban.memory

fun main(args: Array<String>) {
    val config = Config()
//    val producer = Producer(config)
    Web(config).start()
}
