package com.example.stakeholders_service.repository;

import com.example.stakeholders_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);

    public User findByUsername(String username);
    public User findById(long id);

}
