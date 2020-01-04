package com.h2rd.refactoring.usermanagement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("!db-dao")
public class UserDao implements UserOperations {
    private static final Logger LOG = LoggerFactory.getLogger(UserDao.class);

    private Map<String, User> store = new ConcurrentHashMap<>();

    @Override
    public void saveUser(final User user) {
        LOG.debug("user={}", user);

        if (!store.containsKey(user.getEmail())) {
            store.put(user.getEmail(), user);
            LOG.debug("user was added.");
        }
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteUser(final User userToDelete) {
        LOG.debug("userToDelete={}", userToDelete);

        if (store.containsKey(userToDelete.getEmail())) {
            store.remove(userToDelete.getEmail());
            LOG.debug("user was deleted.");
        }
    }

    @Override
    public void updateUser(final User userToUpdate) {
        LOG.debug("userToUpdate={}", userToUpdate);
        final User modified = store.getOrDefault(userToUpdate.getEmail(), null);

        if (modified != null) {
            modified.setName(userToUpdate.getName());
            modified.setRoles(userToUpdate.getRoles());
            LOG.debug("user was updated.");
        }
    }

    @Override
    public List<User> findUser(final String name) {
        LOG.debug("email={}", name);

        return store.values().stream()
                .filter(user -> StringUtils.equals(name, user.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public User findUserById(final String email) {
        LOG.debug("email={}", email);
        return store.getOrDefault(email, null);
    }
}
