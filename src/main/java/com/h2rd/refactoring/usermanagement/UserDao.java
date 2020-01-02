package com.h2rd.refactoring.usermanagement;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDao implements UserOperations {

    private List<User> users = Collections.synchronizedList(new ArrayList<User>());
    ;

    @Override
    public void saveUser(final User user) {
        users.add(user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public void deleteUser(final User userToDelete) {
        users.removeIf(user -> StringUtils.equals(user.getEmail(), userToDelete.getEmail()));
    }

    @Override
    public void updateUser(final User userToUpdate) {
        final User modified = users.stream()
                .filter(user -> StringUtils.equals(userToUpdate.getEmail(), user.getEmail()))
                .findAny()
                .orElse(null);

        if (modified != null) {
            modified.setName(userToUpdate.getName());
            modified.setRoles(userToUpdate.getRoles());
        }
    }

    @Override
    public User findUser(final String name) {
        return users.stream()
                .filter(user -> StringUtils.equals(name, user.getName()))
                .findAny()
                .orElse(null);
    }

    @Override
    public User findUserById(final String email) {
        return users.stream()
                .filter(user -> StringUtils.equals(email, user.getEmail()))
                .findAny()
                .orElse(null);
    }
}
