package ru.aston.util.config;

import org.hibernate.SessionFactory;
import ru.aston.dto.UserDto;
import ru.aston.entity.UserEntity;
import ru.aston.mapper.Mapper;
import ru.aston.mapper.impl.UserMapper;
import ru.aston.repository.UserRepository;
import ru.aston.repository.impl.UserRepositoryImpl;
import ru.aston.service.UserService;
import ru.aston.service.impl.UserServiceImpl;

public final class AppConfig {

    private static final SessionFactory SESSION_FACTORY = HibernateConfig.getSessionFactory();

    private final static UserRepository USER_REPOSITORY = new UserRepositoryImpl(SESSION_FACTORY);

    private final static Mapper<UserDto, UserEntity> MAPPER = new UserMapper();

    private final static UserService USER_SERVICE = new UserServiceImpl(USER_REPOSITORY);

    public static UserService getUserService() {
        return USER_SERVICE;
    }

    public static Mapper<UserDto, UserEntity> getUserMapper() {
        return MAPPER;
    }
}
