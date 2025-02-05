package pt.isel.shipconquest.controller.sse.publisher

import org.springframework.http.MediaType
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

/**
 * Pub/Sub model to implement SSE (Server Sent Events)
 */
class SubscriptionManager(private val subscriptions: ConcurrentHashMap<String, SseEmitter>): SubscriptionManagerAPI {

    override fun publish(key: String, message: Any) {
        subscriptions[key]?.send(message)
    }

    override fun subscribe(key: String): SseEmitter {
        val sse = SseEmitter(Long.MAX_VALUE) // set no timeout
        subscriptions.putIfAbsent(key, sse)
        return sse
    }

    override fun unsubscribe(key: String): String {
        subscriptions[key]?.complete() // close stream
        subscriptions.remove(key)
        return key
    }

    override fun createEvent(id: String, name: String, data: Any): MutableSet<ResponseBodyEmitter.DataWithMediaType> =
        SseEmitter
            .event()
            .id(id)
            .name(name)
            .data(data, MediaType.APPLICATION_JSON)
            .build()
}