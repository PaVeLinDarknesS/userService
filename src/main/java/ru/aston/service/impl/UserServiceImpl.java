package ru.aston.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.dto.UserDto;
import ru.aston.entity.UserEntity;
import ru.aston.exception.DataBaseException;
import ru.aston.exception.ExistingEmailException;
import ru.aston.exception.UserNotFoundException;
import ru.aston.mapper.Mapper;
import ru.aston.repository.UserRepository;
import ru.aston.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final Mapper<UserDto, UserEntity> userMapper;

    public UserServiceImpl(UserRepository userRepository, Mapper<UserDto, UserEntity> mapper) {
        this.userRepository = userRepository;
        this.userMapper = mapper;
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        UserEntity userEntity = getUserById(id);
        return userMapper.entityToDto(userEntity);
    }

    @Override
    public UserDto findByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email = " + email));
        return userMapper.entityToDto(userEntity);
    }

    @Override
    public UserDto insert(UserDto user) {
        checkExistEmail(user.getEmail());
        UserEntity newUser = userMapper.dtoToEntity(user);
        UserEntity savedUser = userRepository.save(newUser);
        LOGGER.info("Created new user with id = '{}'", savedUser.getId());

        return userMapper.entityToDto(savedUser);
    }

    @Override
    public UserDto update(long userId, UserDto user) {
        UserEntity oldUser = getUserById(userId);
        if (!oldUser.getEmail().equals(user.getEmail())) {
            checkExistEmail(user.getEmail());
        }

        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setAge(user.getAge());

        UserEntity updatedUser = userRepository.update(oldUser)
                .orElseThrow(() -> new DataBaseException("Cannot update user with id" + userId));
        LOGGER.info("User was successfully updated with id = '{}'", updatedUser.getId());

        return userMapper.entityToDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        getUserById(id);
        userRepository.delete(id);
        LOGGER.info("User successfully deleted with id = '{}'", id);
    }

    private UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id = " + id));
    }

    private void checkExistEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ExistingEmailException("User exist with email = " + email);
        }
    }
}
