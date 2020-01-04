package com.h2rd.refactoring.web;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserOperations;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("/users")
@Component
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Api(value = "Java Refactoring Test", produces = "application/json")
public class UserResource {
    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    private static final String USER_NOT_FOUND = "User not found.";
    private static final String USER_ALREADY_EXISTS = "User already exists.";

    @Autowired
    private UserOperations userOperations;

    @PostConstruct
    private void checkDI() {
        checkNotNull(userOperations, "userOperations must not be null!");
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(
            value = "Add new user", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Resource was created"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public Response addUser(@ApiParam final User user) {
        LOG.debug("user={}", user);

        final List<String> errors = new UserValidator().validate(user);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(String.join("\n", errors)).build();
        }

        final User existingUser = userOperations.findUserById(user.getEmail());
        if (existingUser == null) {
            userOperations.saveUser(user);
            return Response.status(Response.Status.CREATED).entity(new GenericEntity<User>(user) {
            }).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(USER_ALREADY_EXISTS).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(
            value = "Update user by id (email)", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Resource was updated"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public Response updateUser(@ApiParam final User user) {
        LOG.debug("user={}", user);

        final List<String> errors = new UserValidator().validate(user);

        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(String.join("\n", errors)).build();
        }

        final User existingUser = userOperations.findUserById(user.getEmail());
        if (existingUser != null) {
            userOperations.updateUser(user);
            return Response.ok().entity(new GenericEntity<User>(user) {
            }).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{email}")
    @ApiOperation(
            value = "Delete user by id (email)", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Resource was deleted"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public Response deleteUser(@ApiParam @PathParam("email") final String email) {
        LOG.debug("email={}", email);

        final User user = new User("", email, new ArrayList<>());
        final User existingUser = userOperations.findUserById(user.getEmail());
        if (existingUser != null) {
            userOperations.deleteUser(user);
            return Response.ok().entity(new GenericEntity<User>(existingUser) {
            }).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND).build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(
            value = "Find all users", responseContainer = "List", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Resource found")
    })
    public Response getUsers() {

        final List<User> users = userOperations.getUsers();

        return Response.status(200).entity(new GenericEntity<List<User>>(users) {
        }).build();
    }

    @GET
    @Path("/{name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(
            value = "Search users by name", responseContainer = "List", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Resource found")
    })
    public Response findUser(@ApiParam @PathParam("name") final String name) {
        final List<User> users = userOperations.findUser(name);
        return Response.ok().entity(new GenericEntity<List<User>>(users) {
        }).build();
    }

    @GET
    @Path("/user/{email}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(
            value = "Find user by id (email)", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Resource found"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public Response findUserById(@ApiParam @PathParam("email") final String email) {
        LOG.debug("email={}", email);

        final User user = userOperations.findUserById(email);
        return user != null ? Response.ok().entity(new GenericEntity<User>(user) {
        }).build() : Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND).build();
    }

    private class UserValidator {
        private static final String USER_IS_MANDATORY = "User is mandatory!";
        private static final String EMAIL_IS_MANDATORY = "Email is mandatory!";
        private static final String NAME_IS_MANDATORY = "Name is mandatory!";
        private static final String ONE_ROLE_IS_REQUIRED = "User has to have at least one role!";
        private static final String NO_BLANK_ROLES_ALLOWED = "User has not to have any blank Role (empty string)!";

        private List<String> validate(final User user) {
            final List<String> errors = new ArrayList<>();
            if (user == null) {
                errors.add(USER_IS_MANDATORY);
            } else {
                if (isBlank(user.getEmail())) {
                    errors.add(EMAIL_IS_MANDATORY);
                }

                if (isBlank(user.getName())) {
                    errors.add(NAME_IS_MANDATORY);
                }

                if (user.getRoles() == null || user.getRoles().isEmpty()) {
                    errors.add(ONE_ROLE_IS_REQUIRED);
                }

                if (user.getRoles() != null && user.getRoles().stream().anyMatch(StringUtils::isBlank)) {
                    errors.add(NO_BLANK_ROLES_ALLOWED);
                }
            }

            return errors;
        }
    }
}
