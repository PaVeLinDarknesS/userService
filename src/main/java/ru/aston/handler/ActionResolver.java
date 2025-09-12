package ru.aston.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.dto.UserDto;
import ru.aston.entity.UserEntity;
import ru.aston.enumeration.ActionEnum;
import ru.aston.mapper.Mapper;
import ru.aston.service.UserService;
import ru.aston.util.ValidationUtil;
import ru.aston.util.config.AppConfig;

import java.util.*;

public class ActionResolver {

    private final static Logger LOGGER = LoggerFactory.getLogger(ActionResolver.class);

    private final Scanner scanner;
    private final UserService userService;
    private final Map<ActionEnum, Handler> resolver;
    private final Mapper<UserDto, UserEntity> userMapper;

    public ActionResolver() {

        scanner = new Scanner(System.in);
        userService = AppConfig.getUserService();
        resolver = new EnumMap<>(ActionEnum.class);
        userMapper = AppConfig.getUserMapper();

        resolver.put(ActionEnum.LIST, this::listUser);
        resolver.put(ActionEnum.FIND, this::findUser);
        resolver.put(ActionEnum.INSERT, this::insertUser);
        resolver.put(ActionEnum.UPDATE, this::updateUser);
        resolver.put(ActionEnum.DELETE, this::deleteUser);
        resolver.put(ActionEnum.EXIT, () -> System.exit(0));
    }

    public Handler resolve(ActionEnum action) {
        return resolver.get(action);
    }

    private void listUser() {
        LOGGER.info("Пользователь выбрал список всех User");
        List<UserEntity> users = userService.findAll();

        if (users.isEmpty()) {
            LOGGER.info("Пользователей нет в базе данных");
            System.out.println("Пользователей не найдено в базе данных");
        } else {
            System.out.printf("Найдено %d пользователей:\n", users.size());
            users.stream()
                    .map(userMapper::entityToDto)
                    .forEach(UserDto::print);
        }
    }

    private void findUser() {
        LOGGER.info("Пользователь выбрал поиск User по ID");
        findAndPrintUserById("для поиска");
    }

    private void insertUser() {
        LOGGER.info("Пользователь выбрал insert User");
        if (Objects.nonNull(userService.insert(userMapper.dtoToEntity(fillUserDto())))) {
            System.out.println("Пользователь успешно добавлен");
        }
    }

    private void updateUser() {
        LOGGER.info("Пользователь выбрал update User");
        var existingUserId = findAndPrintUserById("для обновления");
        if (Objects.nonNull(userService.update(existingUserId, userMapper.dtoToEntity(fillUserDto())))) {
            System.out.println("Пользователь успешно изменен");
        }
    }

    private void deleteUser() {
        LOGGER.info("Пользователь выбрал delete User");
        userService.delete(findAndPrintUserById("для удаления"));
        System.out.println("Пользователь успешно удален");
    }

    private UserDto fillUserDto() {
        System.out.println("""
                Заполните построчно данные пользователя (Все поля обязательны к заполнению):
                    Имя
                    Email
                    Возраст (от 16 до 100)
                """);
        String name = scanner.nextLine();
        String email = scanner.nextLine();
        int age = scanner.nextInt();
        scanner.nextLine();
        UserDto userDto = new UserDto(name, email, age);
        ValidationUtil.validate(userDto);
        return userDto;
    }

    private long findAndPrintUserById(String reason) {
        System.out.println("Введите ID пользователя " + reason);
        long userId = scanner.nextLong();
        scanner.nextLine();
        UserEntity existingUser = userService.findById(userId);
        System.out.println("Найденный пользователь: ");
        userMapper.entityToDto(existingUser).print();
        return userId;
    }
}
