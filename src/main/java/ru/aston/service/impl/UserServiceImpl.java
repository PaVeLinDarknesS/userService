package ru.aston.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.entity.UserEntity;
import ru.aston.exception.DataBaseException;
import ru.aston.exception.ExistingEmailException;
import ru.aston.exception.UserNotFoundException;
import ru.aston.repository.UserRepository;
import ru.aston.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity findById(Long id) {
        return getUserById(id);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email = " + email));
    }

    @Override
    public UserEntity insert(UserEntity user) {
        checkExistEmail(user.getEmail());
        UserEntity savedUser = userRepository.save(user);
        LOGGER.info("Created new user with id = '{}'", savedUser.getId());

        return savedUser;
    }

    @Override
    public UserEntity update(long userId, UserEntity user) {
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

        return updatedUser;
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
