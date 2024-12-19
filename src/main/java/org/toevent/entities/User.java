package org.toevent.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @NotNull
    public String name;
    @NotNull
    public String surname;
    @NotNull
    public String birthDate;
    @Email
    public String email;
    public String psw;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
