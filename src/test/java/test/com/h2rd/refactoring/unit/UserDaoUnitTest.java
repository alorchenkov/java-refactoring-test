package test.com.h2rd.refactoring.unit;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserDao;
import com.h2rd.refactoring.usermanagement.UserOperations;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public final class UserDaoUnitTest {

    private final UserOperations userDao = new UserDao();

    @Before
    public void setUp() {
    }

    @Test
    public void saveUserTest() {
        final User user = buildUser();

        userDao.saveUser(user);

        assertEquals(1, userDao.getUsers().size());
        assertEquals(user, userDao.findUser("Fake Name").get(0));

    }

    @Test
    public void deleteUserTest() {
        final User user = buildUser();

        userDao.saveUser(user);

        userDao.deleteUser(user);

        assertEquals(0, userDao.getUsers().size());
    }

    @Test
    public void updateUserTest() {
        final User user = buildUser();

        userDao.saveUser(user);

        final User updated = buildUser();
        updated.setName("Updated");
        userDao.updateUser(updated);

        assertEquals(1, userDao.getUsers().size());
        assertEquals(updated, userDao.findUser("Updated").get(0));

    }

    @Test
    public void getUsersTest() {
        final User user = buildUser();

        userDao.saveUser(user);

        final List<User> users = userDao.getUsers();

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));

    }

    private User buildUser() {
        final User user = new User();
        user.setName("Fake Name");
        user.setEmail("fake@email.com");
        user.setRoles(Arrays.asList("admin", "master"));
        return user;
    }
}