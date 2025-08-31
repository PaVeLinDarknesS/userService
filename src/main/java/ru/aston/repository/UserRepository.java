package ru.aston.repository;

import ru.aston.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<UserEntity> findAll();

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByEmail(String email);

    UserEntity save(UserEntity user);

    Optional<UserEntity> update(UserEntity user);

    void delete(Long id);

}
