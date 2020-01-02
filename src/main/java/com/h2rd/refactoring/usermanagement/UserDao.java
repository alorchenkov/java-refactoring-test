package com.h2rd.refactoring.usermanagement;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UserDao implements UserOperations {
    private Map<String, User> store = new ConcurrentHashMap<>();

    @Override
    public void saveUser(final User user) {
        if (!store.containsKey(user.getEmail())) {
            store.put(user.getEmail(), user);
        }
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteUser(final User userToDelete) {
        if (store.containsKey(userToDelete.getEmail())) {
            store.remove(userToDelete.getEmail());
        }
    }

    @Override
    public void updateUser(final User userToUpdate) {
        final User modified = store.getOrDefault(userToUpdate.getEmail(), null);

        if (modified != null) {
            modified.setName(userToUpdate.getName());
            modified.setRoles(userToUpdate.getRoles());
        }
    }

    @Override
    public List<User> findUser(final String name) {
        return store.values().stream()
                .filter(user -> StringUtils.equals(name, user.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public User findUserById(final String email) {
        return store.getOrDefault(email, null);
    }
}
