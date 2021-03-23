package com.invicto.streaming_platform.services.impl;

import com.invicto.streaming_platform.persistence.model.User;
import com.invicto.streaming_platform.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private UserRepository mockedUserRepository;

    @BeforeEach
    void setUp() {
        mockedUserRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(mockedUserRepository);
    }

    @Test
    void findAll() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();

        List<User> users = new ArrayList<>();

        users.add(user1);
        users.add(user2);
        users.add(user3);

        when(mockedUserRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.findAll();

        assertEquals(users, foundUsers);
        verify(mockedUserRepository, times(1)).findAll();
    }

    @Test
    void updateUser() {
        User user = new User(1L, "login", "adress@gmail.com",
                "passwordHash", LocalDate.now());

        when(mockedUserRepository.save(user)).thenReturn(user);
        when(mockedUserRepository.existsById(user.getId())).thenReturn(true);

        user.setLogin("updatedLogin");
        user.setEmail("updated@gmail.com");
        user.setPasswordHash("updatedPasswordHash");
        user.setDateOfBirth(LocalDate.of(2021, 3, 1));

        User updatedUser = userService.updateUser(user);
        assertEquals(user, updatedUser);

        verify(mockedUserRepository, times(1)).save(user);
    }

    @Test
    void updateOnlyLogin() {
        User user = new User(1L, "login", "adress@gmail.com",
                "passwordHash", LocalDate.now());

        when(mockedUserRepository.save(user)).thenReturn(user);
        when(mockedUserRepository.existsById(user.getId())).thenReturn(true);

        user.setLogin("updatedLogin");

        User updatedUser = userService.updateUser(user);
        assertEquals(user, updatedUser);

        verify(mockedUserRepository, times(1)).save(user);
    }

    @Test
    void updateOnlyEmail() {
        User user = new User(1L, "login", "adress@gmail.com",
                "passwordHash", LocalDate.now());

        when(mockedUserRepository.save(user)).thenReturn(user);
        when(mockedUserRepository.existsById(user.getId())).thenReturn(true);

        user.setEmail("updated@gmail.com");

        User updatedUser = userService.updateUser(user);
        assertEquals(user, updatedUser);

        verify(mockedUserRepository, times(1)).save(user);
    }

    @Test
    void updateOnlyPasswordHash() {
        User user = new User(1L, "login", "adress@gmail.com",
                "passwordHash", LocalDate.now());

        when(mockedUserRepository.save(user)).thenReturn(user);
        when(mockedUserRepository.existsById(user.getId())).thenReturn(true);

        user.setPasswordHash("updatedPasswordHash");

        User updatedUser = userService.updateUser(user);
        assertEquals(user, updatedUser);

        verify(mockedUserRepository, times(1)).save(user);
    }

    @Test
    void updateOnlyDateOfBirth() {
        User user = new User(1L, "login", "adress@gmail.com",
                "passwordHash", LocalDate.now());

        when(mockedUserRepository.save(user)).thenReturn(user);
        when(mockedUserRepository.existsById(user.getId())).thenReturn(true);

        user.setDateOfBirth(LocalDate.of(2021, 3, 1));

        User updatedUser = userService.updateUser(user);
        assertEquals(user, updatedUser);

        verify(mockedUserRepository, times(1)).save(user);
    }

    @Test
    void updateNotExistingUser() {
        User user = new User();

        when(mockedUserRepository.save(user)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    void findByLogin() {
        User user = new User(1L, "testLogin", "test@gmail.com",
                "testPasswordHash", LocalDate.now());
        String login = user.getLogin();

        when(mockedUserRepository.findByLogin(login)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByLogin(login);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
        verify(mockedUserRepository, times(1)).findByLogin(login);
    }

    @Test
    void findByLoginNotExistingUser() {
        String login = "login";
        Optional<User> found = mockedUserRepository.findByLogin(login);

        assertTrue(found.isEmpty());
    }

    @Test
    void findByLoginOrEmailWithLogin() {
        String login = "karl201";
        User expectedUser = new User(1L, "karl201", "user@gmail.com",
                "230d8h34falkfj", LocalDate.of(2000, 2, 2));
        when(mockedUserRepository.findByLogin(login)).thenReturn(Optional.of(expectedUser));
        User actualUser = userService.findByLoginOrEmail(login);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findByLoginOrEmailWithEmail() {
        String email = "karl201@gmail.com";
        User expectedUser = new User(1L, "karl201", "karl201@gmail.com",
                "230d8h34falkfj", LocalDate.of(2000, 2, 2));
        when(mockedUserRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));
        User actualUser = userService.findByLoginOrEmail(email);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findByLoginOrEmailEntityNotExistException() {
        String loginOrEmail = "12df";
        when(mockedUserRepository.findByResetPasswordToken(loginOrEmail)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findByLoginOrEmail(loginOrEmail));
    }

    @Test
    void updateResetPasswordToken() {
        String oldToken = "12023oihf0923jf2039j";
        String newToken = "12023oihf0923jfsdsscj";
        String email = "some.email@ukr.net";
        Optional<User> testOptionalUser = Optional.of(new User(1L, "roman123", email,
                "230d8h34falkfj", oldToken, LocalDate.of(2000, 2, 2)));
        User user = testOptionalUser.get();
        when(mockedUserRepository.findByEmail(email)).thenReturn(testOptionalUser);
        userService.updateResetPasswordToken(newToken, email);
        assertEquals(newToken, user.getResetPasswordToken());
        verify(mockedUserRepository, timeout(1)).save(testOptionalUser.get());
    }

    @Test
    void updateResetPasswordTokenEntityNotExistException() {
        String newToken = "12023oihasdf0923jfsdsscj";
        String email = "13faisl@ukr.net";
        when(mockedUserRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.updateResetPasswordToken(newToken, email));
    }

    @Test
    void findByResetPasswordToken() {
        String token = "12r034gjwojwe9jfsdklw09j";
        User expectedUser = new User(1L, "roman123", "user@gmail.com",
                "230d8h34falkfj", LocalDate.of(2000, 2, 2));
        when(mockedUserRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(expectedUser));
        User actualUser = userService.findByResetPasswordToken(token);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findByResetPasswordTokenEntityNotExistException() {
        String token = "12r034gjwojwejjjdklw09j";
        when(mockedUserRepository.findByResetPasswordToken(token)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findByResetPasswordToken(token));
    }

    @Test
    void updatePasswordHash() {
        String newPasswordHash = "lkjlkhpihpojipojoih";
        User user = new User(1L, "roman123", "user@gmail.com",
                "230d8h34falkfj", LocalDate.of(2000, 2, 2));
        userService.updatePasswordHash(user, newPasswordHash);
        assertEquals(newPasswordHash, user.getPasswordHash());
        verify(mockedUserRepository, times(1)).save(user);
    }

    @Test
    void updatePasswordHashEntityNotExistException() {
        String newPasswordHash = "lkjlkhpihpojipojoih";
        assertThrows(EntityNotFoundException.class, () -> userService.updatePasswordHash(null, newPasswordHash));
    }
}
