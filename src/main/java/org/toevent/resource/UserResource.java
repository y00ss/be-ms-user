package org.toevent.resource;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.toevent.entities.User;
import org.toevent.service.UserService;

import java.net.URI;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api/users")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Tag(name = "users")
public class UserResource {

    private final UserService userService;
    private final Logger logger;

    UserResource(UserService userService, Logger logger) {
        this.userService = userService;
        this.logger = logger;
    }

    @POST
    @Operation(summary = "Creates a valid user")
    @APIResponse(responseCode = "201", description = "The URI of the created user", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @WithTransaction
    public Uni<RestResponse<URI>> createUser(@Valid User user, @Context UriInfo uriInfo) {
        return user.<User>persist()
                .map(userCreated -> {
                    UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(userCreated.id));
                    logger.info("New user created with URI : " + builder.build().toString());
                    return RestResponse.created(builder.build());
                });
    }

    /**
     * Get all users
     */
    @GET
    public Uni<List<User>> getAllUsers() {
        return User.<User>listAll();
    }

    /**
     * Get a user by ID
     */
    @GET
    @Operation(summary = "Returns a user for a given identifier")
    @Path("/{id}")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    @APIResponse(responseCode = "204", description = "The user is not found for a given identifier")
    public Uni<RestResponse<User>> getUserById(@RestPath String id) {
        return userService.getUserById(id)
                .onItem().ifNotNull().transform(RestResponse::ok)
                .onItem().ifNull().continueWith(() -> {
                    logger.infof("User with id %s not found", id);
                    return RestResponse.notFound();
                });
    }

    /**
     * Update an existing user
     */
    @PUT
    @Operation(summary = "Updates an exiting user")
    @APIResponse(responseCode = "200", description = "The updated user", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    @Path("")
    @WithTransaction
    public Uni<User> updateUser(@Valid User user) {
        return User.<User>findById(user.id)
                .map(retrieved -> {
                    retrieved.name = user.name;
                    retrieved.surname = user.surname;
                    retrieved.birthDate = user.birthDate;
                    retrieved.email = user.email;
                    //retrieved.powers = user.powers;
                    return retrieved;
                })
                .map(h -> {
                    logger.debugf("user updated with new valued %s", h);
                    return h;
                });

    }

    /**
     * Delete a user by ID
     */
    @DELETE
    @Operation(summary = "Deletes an exiting user")
    @Path("/{id}")
    @APIResponse(responseCode = "204")
    @WithTransaction
    public Uni<RestResponse<Void>> deleteUser(@PathParam("id") String id) {
        return User.deleteById(id)
                .invoke(() -> logger.debugf("User deleted with %d", id))
                .replaceWith(RestResponse.noContent());
    }
}
