package ru.aston.mapper.impl;

import ru.aston.dto.UserDto;
import ru.aston.entity.UserEntity;
import ru.aston.mapper.Mapper;

public class UserMapper implements Mapper<UserDto, UserEntity> {

    @Override
    public UserEntity dtoToEntity(UserDto dto) {
        return new UserEntity(dto.getName(), dto.getEmail(), dto.getAge());
    }

    @Override
    public UserDto entityToDto(UserEntity entity) {
        return new UserDto(entity.getName(), entity.getEmail(), entity.getAge());
    }
}
