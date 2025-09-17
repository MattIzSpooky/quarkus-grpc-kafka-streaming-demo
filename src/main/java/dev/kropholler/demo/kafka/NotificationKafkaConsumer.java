package dev.kropholler.demo.kafka;

import dev.kropholler.demo.gprc.NotificationGrpcService;
import dev.kropholler.demo.model.NotificationEvent;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class NotificationKafkaConsumer {
    @Inject
    ObjectMapper mapper;

    @Inject
    @GrpcService
    NotificationGrpcService grpcService;

    /**
     * Consume string payloads (JSON) from Kafka 'notifications' topic.
     * Because each Quarkus instance has a unique consumer-group id, every instance receives every message.
     * We forward only to local connections inside NotificationGrpcService.
     */
    @Incoming("notifications-in")
    @Blocking
    public void consume(String json) {
        try {
            NotificationEvent ev = mapper.readValue(json, NotificationEvent.class);
            grpcService.forwardToLocalUser(ev);
        } catch (Exception e) {
            // log and continue; do not let the consumer die
            e.printStackTrace();
        }
    }
}
