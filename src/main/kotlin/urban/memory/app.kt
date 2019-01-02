package urban.memory

import spark.Spark.*

fun main(args: Array<String>) {
    webSocket("/ws", WebSocketHandler::class.java)
    get("/hello") { req, res -> "Hello world!" }
}
