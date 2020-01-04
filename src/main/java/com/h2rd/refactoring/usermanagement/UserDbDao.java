package com.h2rd.refactoring.usermanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Repository
@Profile("db-dao")
public class UserDbDao implements UserOperations {
    private static final Logger LOG = LoggerFactory.getLogger(UserDbDao.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @PostConstruct
    private void checkDI() {
        checkNotNull(jdbcTemplate, "jdbcTemplate must not be null!");
    }


    private static User mapUser(ResultSet rs, int rowNumber) throws SQLException {
        return new User(rs.getString("NAME"), rs.getString("EMAIL"), Arrays.asList(rs.getString("ROLES").split(",")));
    }

    @Override
    @Transactional
    public void saveUser(final User user) {
        LOG.debug("user={}", user);

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("EMAIL", user.getEmail())
                .addValue("NAME", user.getName())
                .addValue("ROLES", String.join(", ", user.getRoles()));
        final int createStatus = jdbcTemplate.update("INSERT INTO USER (EMAIL, NAME, ROLES) VALUES (:EMAIL, :NAME, :ROLES)", namedParameters);

        LOG.debug("createStatus={}", createStatus);
    }

    @Override
    @Transactional
    public List<User> getUsers() {
        final List<User> users = jdbcTemplate.query("SELECT * FROM USER", UserDbDao::mapUser);

        LOG.debug("users.size={}", users.size());
        return users;
    }

    @Override
    @Transactional
    public void deleteUser(final User userToDelete) {
        LOG.debug("userToDelete={}", userToDelete);

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("EMAIL", userToDelete.getEmail());
        final int deleteStatus = jdbcTemplate.update("DELETE FROM USER WHERE EMAIL=:EMAIL", namedParameters);

        LOG.debug("deleteStatus={}", deleteStatus);
    }

    @Override
    @Transactional
    public void updateUser(final User userToUpdate) {
        LOG.debug("userToUpdate={}", userToUpdate);

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("EMAIL", userToUpdate.getEmail())
                .addValue("NAME", userToUpdate.getEmail())
                .addValue("ROLES", String.join(", ", userToUpdate.getRoles()));
        final int updateStatus = jdbcTemplate.update("UPDATE USER SET NAME=:NAME, ROLES=:ROLES WHERE EMAIL=:EMAIL", namedParameters);

        LOG.debug("updateStatus={}", updateStatus);
    }

    @Override
    @Transactional
    public List<User> findUser(final String name) {
        LOG.debug("name={}", name);

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("NAME", name);
        final List<User> result = jdbcTemplate.query("SELECT * FROM USER WHERE NAME=:NAME", namedParameters, UserDbDao::mapUser);

        LOG.debug("result.size={}", result.size());
        return result;
    }

    @Override
    @Transactional
    public User findUserById(final String email) {
        LOG.debug("email={}", email);

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("EMAIL", email);
        final List<User> users = jdbcTemplate.query("SELECT * FROM USER WHERE EMAIL=:EMAIL", namedParameters, UserDbDao::mapUser);
        final User result = !users.isEmpty() ? users.get(0) : null;

        LOG.debug("result={}", result);
        return result;
    }
}
