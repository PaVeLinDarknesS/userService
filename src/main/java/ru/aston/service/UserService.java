package ru.aston.service;

import ru.aston.entity.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> findAll();

    UserEntity findById(Long id);

    UserEntity findByEmail(String email);

    UserEntity insert(UserEntity user);

    UserEntity update(long userId, UserEntity user);

    void delete(Long id);
}
