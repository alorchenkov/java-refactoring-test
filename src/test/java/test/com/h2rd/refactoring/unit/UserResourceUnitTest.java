package test.com.h2rd.refactoring.unit;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserOperations;
import com.h2rd.refactoring.web.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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

        assertEquals(200, response.getStatus());

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
    public void findUser404Test() {
        when(userDao.findUser(eq("test"))).thenReturn(null);
        final Response response = userResource.findUser("test");

        assertEquals(404, response.getStatus());
        assertEquals("User not found.", response.getEntity());

        verify(userDao, times(1)).findUser(eq("test"));
    }

    @Test
    public void findUserTest() {
        final User user = new User();
        when(userDao.findUser(eq("test1"))).thenReturn(user);
        final Response response = userResource.findUser("test1");

        assertEquals(200, response.getStatus());

        assertSame(user, response.getEntity());

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
        verify(userDao, times(0)).findUserById(eq("testemail"));
    }
}
