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
        final List<String> errors = validateUser(user, true, true, true);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors.get(0)).build();
        }

        final User existingUser = userDao.findUserById(user.getEmail());
        if (existingUser == null) {
            userDao.saveUser(user);
            return Response.ok().entity(new GenericEntity<User>(user) {
            }).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(USER_ALREADY_EXISTS).build();
        }
    }

    private List<String> validateUser(final User user, final boolean checkName, final boolean checkEmail, final boolean checkRoles) {
        final List<String> errors = new ArrayList<>();
        if (user == null) {
            errors.add("User is mandatory!");
        } else {
            if (checkEmail && isBlank(user.getEmail())) {
                errors.add("Email is mandatory!");
            }

            if (checkName && isBlank(user.getName())) {
                errors.add("Name is mandatory!");
            }

            if (checkRoles && (user.getRoles() == null || user.getRoles().isEmpty())) {
                errors.add("User has to have at least one role!");
            }
        }

        return errors;
    }

    @PUT
    @Path("/update")
    public Response updateUser(final User user) {
        final List<String> errors = validateUser(user, true, true, true);

        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors.get(0)).build();
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
    @Path("/delete")
    public Response deleteUser(@QueryParam("email") final String email) {
        final User user = new User("", email, new ArrayList<>());
        final List<String> errors = validateUser(user, false, true, false);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors.get(0)).build();
        }

        final User existingUser = userDao.findUserById(user.getEmail());
        if (existingUser != null) {
            userDao.deleteUser(user);
            return Response.ok().entity(new GenericEntity<User>(user) {
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
    @Path("/search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response findUser(@QueryParam("name") final String name) {
        final List<User> users = userDao.findUser(name);
        if (users != null && !users.isEmpty()) {
            return Response.ok().entity(new GenericEntity<List<User>>(users) {
            }).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND).build();
        }
    }
}
