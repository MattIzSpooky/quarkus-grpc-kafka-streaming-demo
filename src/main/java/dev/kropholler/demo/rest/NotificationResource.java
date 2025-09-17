package dev.kropholler.demo.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kropholler.demo.model.NotificationEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import io.smallrye.reactive.messaging.MutinyEmitter;

@Path("/notifications")
@ApplicationScoped
public class NotificationResource {
    @Inject
    @Channel("notifications-create")
    MutinyEmitter<String> emitter;

    @Inject
    ObjectMapper mapper;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Uni<String> send(final NotificationEvent request) throws JsonProcessingException {
        final var json = mapper.writeValueAsString(request);

        return emitter.send(json)
                .map(x -> "ok")
                .onFailure().recoverWithItem("failed");
    }
}
