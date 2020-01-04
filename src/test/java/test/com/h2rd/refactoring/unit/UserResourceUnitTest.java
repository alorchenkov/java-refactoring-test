package test.com.h2rd.refactoring.unit;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserOperations;
import com.h2rd.refactoring.web.UserResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public final class UserResourceUnitTest {
    @Mock
    private UserOperations userDao;

    @InjectMocks
    private UserResource userResource;

    @Test
    public void getUsersTest() {
        when(userDao.getUsers()).thenReturn(new ArrayList<>());
        final Response response = userResource.getUsers();

        assertEquals(200, response.getStatus());
        assertEquals(0, ((Collection<User>) response.getEntity()).size());

        verify(userDao, times(1)).getUsers();
    }

    @Test
    public void saveUserTest() {
        final User user = new User();
        user.setName("testname");
        user.setEmail("testemail");
        user.getRoles().add("testrole");

        final Response response = userResource.addUser(user);

        assertEquals(201, response.getStatus());

        verify(userDao, times(1)).saveUser(same(user));
    }

    @Test
    public void updateUserTest() {
        final User user = new User();
        user.setName("testname");
        user.setEmail("testemail");
        user.getRoles().add("testrole");

        when(userDao.findUserById(eq("testemail"))).thenReturn(user);

        final Response response = userResource.updateUser(user);

        assertEquals(200, response.getStatus());

        verify(userDao, times(1)).updateUser(same(user));
        verify(userDao, times(1)).findUserById(eq("testemail"));
    }


    @Test
    public void deleteUserTest() {
        when(userDao.findUserById(eq("test"))).thenReturn(new User());

        final Response response = userResource.deleteUser("test");

        assertEquals(200, response.getStatus());

        verify(userDao, times(1)).deleteUser(any(User.class));
    }

    @Test
    public void findUserNoUsersTest() {
        when(userDao.findUser(eq("test"))).thenReturn(new ArrayList<>());
        final Response response = userResource.findUser("test");

        assertEquals(200, response.getStatus());
        assertEquals(0, ((List<User>) response.getEntity()).size());

        verify(userDao, times(1)).findUser(eq("test"));
    }

    @Test
    public void findUserTest() {
        final User user = new User();
        when(userDao.findUser(eq("test1"))).thenReturn(Arrays.asList(user));
        final Response response = userResource.findUser("test1");

        assertEquals(200, response.getStatus());
        assertSame(user, ((List<User>) response.getEntity()).get(0));

        verify(userDao, times(1)).findUser(eq("test1"));
    }

    @Test
    public void saveExistingUserTest() {
        final User user = new User();
        user.setName("testname");
        user.setEmail("testemail");
        user.getRoles().add("testrole");

        when(userDao.findUserById(eq("testemail"))).thenReturn(user);
        final Response response = userResource.addUser(user);

        assertEquals(400, response.getStatus());
        assertEquals("User already exists.", response.getEntity());

        verify(userDao, times(0)).saveUser(same(user));
        verify(userDao, times(1)).findUserById(eq("testemail"));
    }

    @Test
    public void saveNoRoleUserTest() {
        final User user = new User();
        user.setName("testname");
        user.setEmail("testemail");

        final Response response = userResource.addUser(user);

        assertEquals(400, response.getStatus());
        assertEquals("User has to have at least one role!", response.getEntity());

        verify(userDao, times(0)).saveUser(same(user));
        verify(userDao, times(0)).findUserById(eq("testemail"));
    }

    @Test
    public void saveNoNameUserTest() {
        final User user = new User();
        user.setName(" ");
        user.setEmail("testemail");
        user.getRoles().add("testrole");

        final Response response = userResource.addUser(user);

        assertEquals(400, response.getStatus());
        assertEquals("Name is mandatory!", response.getEntity());

        verify(userDao, times(0)).saveUser(same(user));
        verify(userDao, times(0)).findUserById(eq("testemail"));
    }

    @Test
    public void saveNoEmailUserTest() {
        final User user = new User();
        user.setName("testname");
        user.setEmail(" ");
        user.getRoles().add("testrole");

        final Response response = userResource.addUser(user);

        assertEquals(400, response.getStatus());
        assertEquals("Email is mandatory!", response.getEntity());

        verify(userDao, times(0)).saveUser(same(user));
        verify(userDao, times(0)).findUserById(anyString());
    }

    @Test
    public void saveWithEmptyRoleUserTest() {
        final User user = new User();
        user.setName("testname");
        user.setEmail("testemail");
        user.getRoles().add("testrole");
        user.getRoles().add(" ");

        final Response response = userResource.addUser(user);

        assertEquals(400, response.getStatus());
        assertEquals("User has not to have any blank Role (empty string)!", response.getEntity());

        verify(userDao, times(0)).saveUser(same(user));
        verify(userDao, times(0)).findUserById(eq("testemail"));
    }

    @Test
    public void saveNonValidUserTest() {
        final User user = new User();

        final Response response = userResource.addUser(user);

        assertEquals(400, response.getStatus());
        assertEquals("Email is mandatory!\n" +
                "Name is mandatory!\n" +
                "User has to have at least one role!", response.getEntity());

        verify(userDao, times(0)).saveUser(same(user));
        verify(userDao, times(0)).findUserById(anyString());
    }

    @Test
    public void saveNullUserTest() {
        final Response response = userResource.addUser(null);

        assertEquals(400, response.getStatus());
        assertEquals("User is mandatory!", response.getEntity());

        verify(userDao, times(0)).saveUser(any(User.class));
        verify(userDao, times(0)).findUserById(anyString());
    }

    @Test
    public void findUserByIdNullTest() {
        when(userDao.findUserById(eq("abc"))).thenReturn(null);

        final Response response = userResource.findUserById("abc");

        assertEquals(404, response.getStatus());
        assertEquals("User not found.", response.getEntity());

        verify(userDao, times(1)).findUserById(eq("abc"));
    }

    @Test
    public void findUserByIdTest() {
        final User user = new User();

        when(userDao.findUserById(eq("abc"))).thenReturn(user);

        final Response response = userResource.findUserById("abc");

        assertEquals(200, response.getStatus());
        assertSame(user, response.getEntity());

        verify(userDao, times(1)).findUserById(eq("abc"));
    }
}
