package pl.noteally;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import pl.noteally.domain.Catalog;
import pl.noteally.domain.Role;
import pl.noteally.domain._User;
import pl.noteally.repositories.CatalogRepository;
import pl.noteally.repositories.SharedRepository;
import pl.noteally.repositories.UserRepository;
import pl.noteally.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private SharedRepository sharedRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        catalogRepository.deleteAll();
        sharedRepository.deleteAll();
    }

    @Test
    public void testGetUsers() {
        _User user1 = createUser("user1@example.com", "John", "Doe", 25);
        _User user2 = createUser("user2@example.com", "Jane", "Doe", 30);

        List<_User> users = userService.getUsers();
        assertEquals(2, users.size());
    }

    @Test
    public void testGetUserById() {
        _User user = createUser("user@example.com", "John", "Doe", 25);

        _User foundUser = userService.getUserById(user.getId());
        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    public void testGetUserByEmail() {
        _User user = createUser("user@example.com", "John", "Doe", 25);

        _User foundUser = userService.getUserByEmail("user@example.com");
        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    public void testSignUpUser() {
        _User user = new _User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setName("John");
        user.setSurname("Doe");
        user.setAge(25);

        userService.signUpUser(user);

        _User foundUser = userService.getUserByEmail("user@example.com");
        assertNotNull(foundUser);
        assertNotEquals("password", foundUser.getPassword());

        List<Catalog> catalogs = catalogRepository.findByUserId(foundUser.getId());
        assertEquals(2, catalogs.size());
    }

    @Test
    public void testLoadUserByUsername() {
        _User user = createUser("user@example.com", "John", "Doe", 25);

        UserDetails userDetails = userService.loadUserByUsername("user@example.com");
        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    public void testUpdateUser() {
        _User user = createUser("user@example.com", "John", "Doe", 25);

        _User updatedUser = new _User();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated");
        updatedUser.setSurname("User");
        updatedUser.setAge(30);
        updatedUser.setRole(Role.USER);

        userService.updateUser(updatedUser, user.getId());

        _User foundUser = userService.getUserById(user.getId());
        assertNotNull(foundUser);
        assertEquals("updated@example.com", foundUser.getEmail());
        assertEquals("Updated", foundUser.getName());
        assertEquals("User", foundUser.getSurname());
        assertEquals(30, foundUser.getAge());
        assertEquals(Role.USER, foundUser.getRole());
    }

    @Test
    public void testDeleteUserById() {
        _User user = createUser("user@example.com", "John", "Doe", 25);

        userService.deleteUserById(user.getId());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(user.getId());
        });
    }

    @Test
    public void testUserExists() {
        _User user = new _User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setName("John");
        user.setSurname("Doe");
        user.setAge(25);

        assertFalse(userService.userExists(user));

        userService.signUpUser(user);

        assertTrue(userService.userExists(user));
    }

    @Test
    public void testValidationErrors() {
        _User invalidUser = new _User();
        invalidUser.setEmail("invalid-email");
        invalidUser.setPassword("123");
        invalidUser.setName("john");
        invalidUser.setSurname("doe");
        invalidUser.setAge(17);

        assertThrows(TransactionSystemException.class, () -> {
            userService.signUpUser(invalidUser);
        });
    }

    private _User createUser(String email, String name, String surname, int age) {
        _User user = new _User();
        user.setEmail(email);
        user.setPassword("password");
        user.setName(name);
        user.setSurname(surname);
        user.setAge(age);
        return userRepository.save(user);
    }
}
