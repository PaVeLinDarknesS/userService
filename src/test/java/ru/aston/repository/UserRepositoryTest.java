package ru.aston.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.aston.entity.UserEntity;
import ru.aston.exception.UserNotFoundException;
import ru.aston.repository.impl.UserRepositoryImpl;
import ru.aston.util.SqlScriptRunner;
import ru.aston.util.config.HibernateConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {

    private static final String testDataSqlPath = "src/test/resources/testData.sql";
    private static final String clearTableSqlPath = "src/test/resources/clearTable.sql";
    private static final int COUNT_USERS_IN_DB = 3;
    private static final String EXIST_EMAIL = "joshua.bloch@example.com";

    private static SessionFactory sessionFactory;
    private static PostgreSQLContainer<?> postgres;
    private static UserRepository userRepository;

    @BeforeAll
    public static void startContainer() {

        postgres = new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("testdb");
        postgres.start();

        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
        System.setProperty("app.environment", "test"); // Для HibernateConfig

        sessionFactory = HibernateConfig.getSessionFactory();

        userRepository = new UserRepositoryImpl(sessionFactory);
    }

    @AfterAll
    public static void stopContainer() {
        HibernateConfig.shutdown();
        if (postgres != null) {
            postgres.stop();
        }
    }

    @BeforeEach
    public void fillDataBase() {
        SqlScriptRunner.executeSql(sessionFactory, testDataSqlPath);
    }

    @AfterEach
    public void clearDataBase() {
        SqlScriptRunner.executeSql(sessionFactory, clearTableSqlPath);
    }

    @Test
    public void findAll_whenOk_listWithUsers() {
        List<UserEntity> actualUserEntities = userRepository.findAll();

        assertEquals(COUNT_USERS_IN_DB, actualUserEntities.size());
    }

    @Test
    public void findById_whenIdExist_userWithId() {
        long userId = COUNT_USERS_IN_DB - 1;

        Optional<UserEntity> actualUser = userRepository.findById(userId);

        assertTrue(actualUser.isPresent());
        assertEquals(userId, actualUser.get().getId());
    }

    @Test
    public void findById_whenIdNotExist_emptyOptional() {
        long userId = COUNT_USERS_IN_DB + 1;

        Optional<UserEntity> actualUser = userRepository.findById(userId);

        assertTrue(actualUser.isEmpty());
    }

    @Test
    public void findByEmail_whenEmailExist_userWithId() {
        Optional<UserEntity> actualUser = userRepository.findByEmail(EXIST_EMAIL);

        assertTrue(actualUser.isPresent());
    }

    @Test
    public void findByEmail_whenEmailNotExist_emptyOptional() {
        String email = "notExist@email.com";

        Optional<UserEntity> actualUser = userRepository.findByEmail(email);

        assertTrue(actualUser.isEmpty());
    }

    @Test
    public void save_whenEmailNotExist_userWithId() {
        String email = "notExist@email.com";
        UserEntity user = getUser();
        user.setEmail(email);

        UserEntity actualUser = userRepository.save(user);

        assertNotNull(actualUser.getId());
        assertEquals(email, actualUser.getEmail());
    }

    @Test
    public void save_whenEmailExist_userWithoutId() {
        UserEntity user = getUser();
        user.setEmail(EXIST_EMAIL);

        UserEntity actualUser = userRepository.save(user);

        assertNull(actualUser.getId());
        assertEquals(EXIST_EMAIL, actualUser.getEmail());
    }

    @Test
    public void delete_whenIdExist() {
        long existId = COUNT_USERS_IN_DB - 1;
        int countUsersInDbBeforeDelete = userRepository.findAll().size();

        userRepository.delete(existId);
        int countUsersInDbAfterDelete = userRepository.findAll().size();
        Optional<UserEntity> findDeletedUser = userRepository.findById(existId);

        assertEquals(1, countUsersInDbBeforeDelete - countUsersInDbAfterDelete);
        assertTrue(findDeletedUser.isEmpty());
    }

    @Test
    public void delete_whenIdNotExist_throwUserNotFoundException() {
        long notExistId = COUNT_USERS_IN_DB + 1;
        int countUsersInDbBeforeDelete = userRepository.findAll().size();

        assertThrows(UserNotFoundException.class, () -> userRepository.delete(notExistId));

        int countUsersInDbAfterDelete = userRepository.findAll().size();
        assertEquals(countUsersInDbBeforeDelete, countUsersInDbAfterDelete);
    }

    @Test
    public void update_whenEmailNotExist_updatedUser() {
        String email = "notExist@email.com";
        UserEntity savedUser = userRepository.save(getUser());
        savedUser.setEmail(email);

        Optional<UserEntity> actualUser = userRepository.update(savedUser);
        savedUser = userRepository.findById(savedUser.getId()).get();

        assertTrue(actualUser.isPresent());
        assertEquals(email, actualUser.get().getEmail());
        assertEquals(email, savedUser.getEmail());
    }

    @Test
    public void update_whenEmailExist_emptyOptional() {
        UserEntity savedUser = userRepository.save(getUser());
        savedUser.setEmail(EXIST_EMAIL);

        Optional<UserEntity> actualUser = userRepository.update(savedUser);
        savedUser = userRepository.findById(savedUser.getId()).get();

        assertTrue(actualUser.isEmpty());
        assertNotEquals(EXIST_EMAIL, savedUser.getEmail());
    }

    private UserEntity getUser() {
        return UserEntity.builder()
                .name("Nikolay")
                .email("testEmail@mail.com")
                .age(19)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
