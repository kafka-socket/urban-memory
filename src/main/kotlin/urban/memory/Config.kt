package urban.memory

import com.typesafe.config.ConfigFactory

object Config {
    private val config = ConfigFactory.load()

    val port: Int = config.getInt("urban-memory.port")
    val heartbeatIntervalMillis: Long = config.getLong("urban-memory.heartbeat-interval-millis")
    val jwtSecret: String = config.getString("urban-memory.jwt-secret")
    val userClaimKey: String = config.getString("urban-memory.user-claim-key")

    val kafka = config.getConfig("urban-memory.kafka")
}
