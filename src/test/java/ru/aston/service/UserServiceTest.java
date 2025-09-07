package ru.aston.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.entity.UserEntity;
import ru.aston.exception.ExistingEmailException;
import ru.aston.exception.UserNotFoundException;
import ru.aston.repository.UserRepository;
import ru.aston.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private final UserService userService = new UserServiceImpl(userRepository);

    private List<UserEntity> userEntityList;

    @BeforeEach
    public void init() {
        userEntityList = new LinkedList<>();

        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .name("Vasua")
                .email("Vasua@vasua.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .name("Katya")
                .email("Katua@katua.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity user3 = UserEntity.builder()
                .id(3L)
                .name("Vitalik")
                .email("Vitalik@vitalic.com")
                .age(18)
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity user4 = UserEntity.builder()
                .id(4L)
                .name("Klara")
                .email("Klara@Klara.com")
                .age(27)
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity user5 = UserEntity.builder()
                .id(5L)
                .name("Anya")
                .email("Anya@Anya.com")
                .age(55)
                .createdAt(LocalDateTime.now())
                .build();

        userEntityList.add(user1);
        userEntityList.add(user2);
        userEntityList.add(user3);
        userEntityList.add(user4);
        userEntityList.add(user5);
    }

    @AfterEach
    public void destroy() {
        userEntityList = new LinkedList<>();
    }

    @Test
    public void findAll_whenOk() {
        BDDMockito.given(userRepository.findAll())
                .willReturn(userEntityList);

        List<UserEntity> expectedUser = userEntityList;
        List<UserEntity> actualUser = userService.findAll();

        assertEquals(expectedUser.size(), actualUser.size());
        verify(userRepository).findAll();
    }

    @Test
    public void findById_whenIdIsExist_userEntityWithId() {
        long id = 1L;

        BDDMockito.given(userRepository.findById(id))
                .willReturn(Optional.ofNullable(userEntityList.get(1)));

        UserEntity expectedUser = userEntityList.get(1);
        UserEntity actualUser = userService.findById(id);

        assertNotNull(actualUser);
        assertNotNull(actualUser.getId());
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());

        verify(userRepository).findById(id);
    }

    @Test
    public void findById_whenIdIsNotExist_throwsUserNotFoundException() {
        long id = 2000L;

        BDDMockito.given(userRepository.findById(id))
                .willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(id));

        verify(userRepository).findById(id);
    }

    @Test
    public void findByEmail_whenEmailIsExist_userEntityWithId() {
        int id = 3;
        String email = userEntityList.get(id).getEmail();

        BDDMockito.given(userRepository.findByEmail(email))
                .willReturn(Optional.ofNullable(userEntityList.get(id)));

        UserEntity expectedUser = userEntityList.get(id);
        UserEntity actualUser = userService.findByEmail(email);

        assertNotNull(actualUser);
        assertNotNull(actualUser.getId());
        assertEquals(expectedUser, actualUser);
        assertEquals(email, actualUser.getEmail());

        verify(userRepository).findByEmail(email);
    }

    @Test
    public void findByEmail_whenEmailIsNotExist_throwsUserNotFoundException() {
        String email = userEntityList.get(3).getEmail();

        BDDMockito.given(userRepository.findByEmail(email))
                .willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(email));

        verify(userRepository).findByEmail(email);
    }

    @Test
    public void delete_whenIdIsExist() {
        long id = 1L;

        BDDMockito.given(userRepository.findById(id))
                .willReturn(Optional.of(userEntityList.get(1)));

        userService.delete(id);

        verify(userRepository).findById(id);
        verify(userRepository).delete(id);
    }

    @Test
    public void delete_whenIdIsNotExist_throwsUserNotFoundException() {
        long id = 2000L;

        BDDMockito.given(userRepository.findById(id))
                .willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(id));

        verify(userRepository).findById(id);
        verify(userRepository, never()).delete(id);
    }

    @Test
    public void insert_whenEmailIsNotExist() {
        UserEntity insertUser = getUser();

        UserEntity savedUser = getUserWithId(10L);

        BDDMockito.given(userRepository.findByEmail(insertUser.getEmail()))
                .willReturn(Optional.empty());
        BDDMockito.given(userRepository.save(insertUser))
                .willReturn(savedUser);

        userService.insert(insertUser);

        verify(userRepository).findByEmail(insertUser.getEmail());
        verify(userRepository).save(insertUser);
    }

    @Test
    public void insert_whenEmailIsExist_throwsExistingEmailException() {
        int id = 2;
        String email = userEntityList.get(id).getEmail();

        UserEntity insertUser = getUser();
        insertUser.setEmail(email);

        BDDMockito.given(userRepository.findByEmail(email))
                .willReturn(Optional.of(userEntityList.get(id)));

        assertThrows(ExistingEmailException.class, () -> userService.insert(insertUser));

        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(insertUser);
    }

    @Test
    public void update_whenIdIsExistAndEmailNotExist() {
        long id = 2;
        UserEntity newUser = getUser();
        UserEntity expectedUser = copyOfUser(userEntityList.get((int) id));
        expectedUser.setName(newUser.getName());
        expectedUser.setEmail(newUser.getEmail());
        expectedUser.setAge(newUser.getAge());

        BDDMockito.given(userRepository.findById(id))
                .willReturn(Optional.of(userEntityList.get((int) id)));
        BDDMockito.given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        BDDMockito.given(userRepository.update(any(UserEntity.class)))
                .willReturn(Optional.of(expectedUser));

        UserEntity actualUser = userService.update(id, newUser);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getAge(), actualUser.getAge());

        verify(userRepository).findById(id);
        verify(userRepository).findByEmail(newUser.getEmail());
        verify(userRepository).update(any(UserEntity.class));
    }

    @Test
    public void update_whenIdIsExistAndEmailExist_throwExistingEmailException() {
        long id = 2;
        UserEntity newUser = getUser();
        UserEntity expectedUser = copyOfUser(userEntityList.get((int) id));
        expectedUser.setName(newUser.getName());
        expectedUser.setEmail(newUser.getEmail());
        expectedUser.setAge(newUser.getAge());

        BDDMockito.given(userRepository.findById(id))
                .willReturn(Optional.of(userEntityList.get((int) id)));
        BDDMockito.given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(userEntityList.get((int) id)));
        BDDMockito.given(userRepository.update(any(UserEntity.class)))
                .willReturn(Optional.of(expectedUser));

        assertThrows(ExistingEmailException.class, () -> userService.update(id, newUser));

        verify(userRepository).findById(id);
        verify(userRepository).findByEmail(newUser.getEmail());
        verify(userRepository, never()).update(any(UserEntity.class));
    }

    @Test
    public void update_whenIdNotExist_throwUserNotFoundException() {
        long id = 2;
        UserEntity newUser = getUser();
        UserEntity expectedUser = copyOfUser(userEntityList.get((int) id));
        expectedUser.setName(newUser.getName());
        expectedUser.setEmail(newUser.getEmail());
        expectedUser.setAge(newUser.getAge());

        BDDMockito.given(userRepository.findById(id))
                .willReturn(Optional.empty());
        BDDMockito.given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(userEntityList.get((int) id)));
        BDDMockito.given(userRepository.update(any(UserEntity.class)))
                .willReturn(Optional.of(expectedUser));

        assertThrows(UserNotFoundException.class, () -> userService.update(id, newUser));

        verify(userRepository).findById(id);
        verify(userRepository, never()).findByEmail(newUser.getEmail());
        verify(userRepository, never()).update(any(UserEntity.class));
    }

    private UserEntity getUser() {
        return UserEntity.builder()
                .name("Dobryna")
                .email("dobryna@mail.com")
                .age(37)
                .build();
    }

    private UserEntity getUserWithId(long id) {
        UserEntity user = getUser();
        user.setId(id);
        return user;
    }

    private UserEntity copyOfUser(UserEntity userEntity) {
        return UserEntity.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .age(userEntity.getAge())
                .createdAt(userEntity.getCreatedAt())
                .build();
    }
}
