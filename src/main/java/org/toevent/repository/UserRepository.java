package org.toevent.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.toevent.entities.User;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
 /**/
}
