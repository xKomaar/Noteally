package pl.noteally.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.noteally.domain.Catalog;
import pl.noteally.domain.SharedNote;
import pl.noteally.domain._User;
import pl.noteally.repositories.CatalogRepository;
import pl.noteally.repositories.SharedRepository;
import pl.noteally.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final SharedRepository sharedRepository;
    private final CatalogRepository catalogRepository;
    private final BCryptPasswordEncoder bCryptpasswordEncoder;

    public List<_User> getUsers() {
        return userRepository.findAll();
    }

    public _User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMsg = "User not found with id: " + id;
                    logger.error("UserService.getUserById(): {}", errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });
    }

    public _User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    String errorMsg = "User not found with email: " + email;
                    logger.error("UserService.getUserByEmail(): {}", errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });
    }

    public void signUpUser(_User user) {
        if (userExists(user)) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        String encodedPassword = bCryptpasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        // default catalogs
        Catalog defaultCatalog = new Catalog();
        defaultCatalog.setName("default");
        defaultCatalog.setUser(user);
        catalogRepository.save(defaultCatalog);

        Catalog sharedCatalog = new Catalog();
        sharedCatalog.setName("shared");
        sharedCatalog.setUser(user);
        catalogRepository.save(sharedCatalog);

        logger.info("UserService.signUpUser(): User signed up successfully with email: {}", user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    String errorMsg = "User not found with email: " + email;
                    logger.warn("UserService.loadUserByUsername(): {}", errorMsg);
                    return new UsernameNotFoundException("Incorrect password or email");
                });
    }
    @Transactional
    public void updateUser(_User user, Long userId) {
        _User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMsg = "User not found with id: " + userId;
                    logger.error("UserService.updateUser(): {}", errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });

        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setAge(user.getAge());
        existingUser.setRole(user.getRole());

        userRepository.save(existingUser);
        logger.info("UserService.updateUser(): User with id: {} updated successfully", userId);
    }
    @Transactional
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            String errorMsg = "User not found with id: " + userId;
            logger.error("UserService.deleteUserById(): {}", errorMsg);
            throw new EntityNotFoundException(errorMsg);
        }

        List<SharedNote> list = sharedRepository.findRelatedNotes(userId);
        sharedRepository.deleteAll(list);
        userRepository.deleteById(userId);
        logger.info("UserService.deleteUserById(): User with id: {} deleted successfully", userId);
    }

    public boolean userExists(_User user) {
        return userRepository.findByEmail(user.getEmail()).isPresent();
    }

    public List<_User> getUserListWithoutSelf(HttpSession session) {
        List<_User> userList = getUsers();
        List<_User> filteredUserList = new ArrayList<>();
        for (_User user : userList) {
            if (!user.getId().equals(session.getAttribute("userId"))) {
                filteredUserList.add(user);
            }
        }
        return filteredUserList;
    }
}
