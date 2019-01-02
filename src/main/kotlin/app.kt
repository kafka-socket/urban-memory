import spark.Spark.*
import urban.memory.WebSocketHandler

fun main(args: Array<String>) {
    webSocket("/ws", WebSocketHandler::class.java)
    get("/hello") { req, res -> "Hello world!" }
}
