package com.newstickr.newstickr.user.repository;

import com.newstickr.newstickr.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}