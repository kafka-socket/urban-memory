package urban.memory

import com.typesafe.config.ConfigFactory

class Config {
    private val config = ConfigFactory.load()

    val port: Int = config.getInt("urban-memory.port")
    val heartbeatIntervalMillis: Long = config.getLong("urban-memory.heartbeat-interval-millis")
    val jwtSecret: String = config.getString("urban-memory.jwt-secret")
    val jwtUserKey: String = config.getString("urban-memory.jwt-user-key")
}
