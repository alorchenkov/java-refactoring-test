package com.h2rd.refactoring.usermanagement;

import java.util.List;

public interface UserOperations {
    void saveUser(User user);

    List<User> getUsers();

    void deleteUser(User userToDelete);

    void updateUser(User userToUpdate);

    User findUser(String name);
}
