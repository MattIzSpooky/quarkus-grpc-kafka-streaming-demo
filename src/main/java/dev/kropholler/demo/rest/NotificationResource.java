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
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/notifications")
@ApplicationScoped
public class NotificationResource {
    @Inject
    @Channel("notifications-out")
    Emitter<String> emitter;

    @Inject
    ObjectMapper mapper;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> send(final NotificationEvent request) throws JsonProcessingException {
        final var json = mapper.writeValueAsString(request);

        return Uni.createFrom().completionStage(emitter.send(json))
                .replaceWith(Response.accepted().build());
    }
}
