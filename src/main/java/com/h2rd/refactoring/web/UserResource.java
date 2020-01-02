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

@Path("/users")
@Component
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class UserResource {

    private static final String USER_NOT_FOUND = "User not found.";

    @Autowired
    private UserOperations userDao;

    @PostConstruct
    private void checkDI() {
        checkNotNull(userDao, "userDao must not be null!");
    }

    @POST
    @Path("/add")
    public Response addUser(final User user) {
        validateUser(user);
        userDao.saveUser(user);
        return Response.ok().entity(user).build();
    }

    private void validateUser(final User user) {

    }

    @PUT
    @Path("/update")
    public Response updateUser(final User user) {
        //validateRoles(user);
        userDao.updateUser(user);
        return Response.ok().entity(user).build();
    }

    @DELETE
    @Path("/delete")
    public Response deleteUser(@QueryParam("email") final String email) {

        final User user = new User("", email, new ArrayList<>());
        //validateEmail(email)

        userDao.deleteUser(user);

        return Response.ok().entity(user).build();
    }

    @GET
    @Path("/find")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUsers() {

        final List<User> users = userDao.getUsers();

        final GenericEntity<List<User>> usersEntity = new GenericEntity<List<User>>(users) {
        };

        return Response.status(200).entity(usersEntity).build();
    }

    @GET
    @Path("/search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response findUser(@QueryParam("name") final String name) {
        //validateEmail(name)

        final User user = userDao.findUser(name);
        if (user != null) {
            return Response.ok().entity(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND).build();
        }
    }
}
