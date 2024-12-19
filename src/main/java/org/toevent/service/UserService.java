package org.toevent.service;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.toevent.entities.User;
import org.toevent.repository.UserRepository;

import java.util.List;

@ApplicationScoped
public class UserService {

    private final Logger logger;
    private final UserRepository userRepository;

    UserService(Logger logger, UserRepository userRepository){
        this.logger = logger;
        this.userRepository = userRepository;
    }

    @WithTransaction
    public Uni<User> createUser(User user) {
        return  userRepository.persist(user);
    }

    @WithSession
    public Uni<List<User>> getAllUsers() {
        return userRepository.listAll();
    }

    @WithSession
    public Uni<User> getUserById(String id) {
        return userRepository.findById(Long.valueOf(id));
    }

}
