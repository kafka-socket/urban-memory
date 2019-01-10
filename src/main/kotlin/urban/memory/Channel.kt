package urban.memory

interface Channel {
    val key: String
    fun send(message: String)
}
