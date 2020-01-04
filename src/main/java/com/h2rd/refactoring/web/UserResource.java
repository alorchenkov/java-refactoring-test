package com.h2rd.refactoring.web;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserOperations;
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
public class UserResource {

    private static final String USER_NOT_FOUND = "User not found.";
    private static final String USER_ALREADY_EXISTS = "User already exists.";

    @Autowired
    private UserOperations userDao;

    @PostConstruct
    private void checkDI() {
        checkNotNull(userDao, "userDao must not be null!");
    }

    @POST
    @Path("/add")
    public Response addUser(final User user) {
        final List<String> errors = validateUser(user);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(String.join("\n", errors)).build();
        }

        final User existingUser = userDao.findUserById(user.getEmail());
        if (existingUser == null) {
            userDao.saveUser(user);
            return Response.status(Response.Status.CREATED).entity(new GenericEntity<User>(user) {
            }).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(USER_ALREADY_EXISTS).build();
        }
    }

    private List<String> validateUser(final User user) {
        final List<String> errors = new ArrayList<>();
        if (user == null) {
            errors.add("User is mandatory!");
        } else {
            if (isBlank(user.getEmail())) {
                errors.add("Email is mandatory!");
            }

            if (isBlank(user.getName())) {
                errors.add("Name is mandatory!");
            }

            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                errors.add("User has to have at least one role!");
            }
        }

        return errors;
    }

    @PUT
    @Path("/update")
    public Response updateUser(final User user) {
        final List<String> errors = validateUser(user);

        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(String.join("\n", errors)).build();
        }

        final User existingUser = userDao.findUserById(user.getEmail());
        if (existingUser != null) {
            userDao.updateUser(user);
            return Response.ok().entity(new GenericEntity<User>(user) {
            }).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/delete/{email}")
    public Response deleteUser(@PathParam("email") final String email) {
        final User user = new User("", email, new ArrayList<>());
        final User existingUser = userDao.findUserById(user.getEmail());
        if (existingUser != null) {
            userDao.deleteUser(user);
            return Response.ok().entity(new GenericEntity<User>(existingUser) {
            }).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND).build();
        }
    }

    @GET
    @Path("/find")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUsers() {

        final List<User> users = userDao.getUsers();

        return Response.status(200).entity(new GenericEntity<List<User>>(users) {
        }).build();
    }

    @GET
    @Path("/search/{name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response findUser(@PathParam("name") final String name) {
        final List<User> users = userDao.findUser(name);
        return Response.ok().entity(new GenericEntity<List<User>>(users) {
        }).build();
    }

    @GET
    @Path("/{email}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response findUserById(@PathParam("email") final String email) {
        final User user = userDao.findUserById(email);
        return user != null ? Response.ok().entity(new GenericEntity<User>(user) {
        }).build() : Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND).build();
    }
}
