package urban.memory

import java.util.concurrent.ConcurrentHashMap

object Channels {
    private val channels: ConcurrentHashMap<
            String,
            ConcurrentHashMap.KeySetView<Channel, Boolean>
            > = ConcurrentHashMap()

    fun add(channel: Channel) {
        val key = channel.key
        if (channels.containsKey(key)) {
            channels[key]?.add(channel)
        }
        else {
            val set: ConcurrentHashMap.KeySetView<Channel, Boolean> = ConcurrentHashMap.newKeySet()
            set.add(channel)
            channels[key] = set
        }
    }

    fun remove(channel: Channel) {
        channels[channel.key]?.remove(channel)
    }

    fun byKey(key: String) : ConcurrentHashMap.KeySetView<Channel, Boolean>? {
        return channels[key]
    }
}
