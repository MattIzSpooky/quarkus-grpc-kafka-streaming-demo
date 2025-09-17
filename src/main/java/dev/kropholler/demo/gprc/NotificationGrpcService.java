package dev.kropholler.demo.gprc;

import dev.kropholler.demo.grpc.generated.Notification;
import dev.kropholler.demo.grpc.generated.NotificationService;
import dev.kropholler.demo.grpc.generated.NotificationsSubscribeRequest;
import dev.kropholler.demo.model.NotificationEvent;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
@Singleton
public class NotificationGrpcService implements NotificationService {

    // active local subscribers: userId -> emitter
    private final Map<String, MultiEmitter<? super Notification>> emitters = new ConcurrentHashMap<>();

    /**
     * Called when a client does: stub.streamNotifications(UserId.newBuilder().setId("bob").build())
     * Returns a Multi stream that will emit notifications destined for that user.
     */
    @Override
    public Multi<Notification> streamNotifications(NotificationsSubscribeRequest request) {
        String userId = request.getUserId();

        return Multi.createFrom().emitter(emitter -> {
            // register emitter for this user
            emitters.put(userId, emitter);

            // cleanup when client cancels or completes
            emitter.onTermination(() -> emitters.remove(userId));
        });
    }

    /**
     * Called by the Kafka consumer to forward an event to a connected user (if connected locally).
     */
    public void forwardToLocalUser(NotificationEvent event) {
        MultiEmitter<? super Notification> emitter = emitters.get(event.getUserId());
        if (emitter != null) {
            Notification n = Notification.newBuilder()
                    .setUserId(event.getUserId())
                    .setMessage(event.getMessage())
                    .setTimestamp(Instant.now().toEpochMilli())
                    .build();

            // emit, but catch any failure so we don't crash
            try {
                emitter.emit(n);
            } catch (Throwable t) {
                // emitter may throw if downstream cancelled or backpressure issues
                // remove emitter to be safe; it will be recreated on reconnect
                emitters.remove(event.getUserId());
            }
        }
    }
}
