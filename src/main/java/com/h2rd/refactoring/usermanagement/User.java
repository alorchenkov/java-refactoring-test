package com.h2rd.refactoring.usermanagement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public final class User {

    private String name;
    private String email;
    private List<String> roles = new ArrayList<>();

    public User() {
    }

    public User(final String name, final String email, final List<String> roles) {
        this.name = name;
        this.email = email;
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, roles);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
