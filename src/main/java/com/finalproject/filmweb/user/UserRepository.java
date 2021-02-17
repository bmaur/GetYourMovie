package com.finalproject.filmweb.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByUserNick(String userNick);

    Optional<UserEntity> findByConfirmationToken(String confirmationToken);

}
