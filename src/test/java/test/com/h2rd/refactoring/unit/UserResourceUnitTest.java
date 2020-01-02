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
        final Response response = userResource.addUser(user);

        assertEquals(200, response.getStatus());

        verify(userDao, times(1)).saveUser(same(user));
    }

    @Test
    public void updateUserTest() {
        final User user = new User();
        final Response response = userResource.updateUser(user);

        assertEquals(200, response.getStatus());

        verify(userDao, times(1)).updateUser(same(user));
    }


    @Test
    public void deleteUserTest() {
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
}
