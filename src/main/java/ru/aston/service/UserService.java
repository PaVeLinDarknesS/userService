package ru.aston.service;

import ru.aston.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(Long id);

    UserDto findByEmail(String email);

    UserDto insert(UserDto user);

    UserDto update(long userId, UserDto user);

    void delete(Long id);
}
