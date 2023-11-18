package pl.noteally.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.noteally.data.Catalog;
import pl.noteally.data.SharedNote;
import pl.noteally.data.User;
import pl.noteally.repositories.CatalogRepository;
import pl.noteally.repositories.SharedRepository;
import pl.noteally.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final SharedRepository sharedRepository;
    private final CatalogRepository catalogRepository;
    private final BCryptPasswordEncoder bCryptpasswordEncoder;
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByLogin(username);
    }

    public String signUpUser(User user){

        String encodedPassword = bCryptpasswordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);

        //default catalogs
        Catalog defaultCatalog = new Catalog();
        defaultCatalog.setName("default");
        defaultCatalog.setUser(user);
        catalogRepository.save(defaultCatalog);

        Catalog sharedCatalog = new Catalog();
        sharedCatalog.setName("shared");
        sharedCatalog.setUser(user);
        catalogRepository.save(sharedCatalog);

        return "catalogs";
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLogin(login);
        if(user.isPresent())
        {
            return user.get();
        }
        else throw new UsernameNotFoundException("Niepoprawne hasło lub nazwa użytkownika");
    }

    public void updateUser(User user, Integer userId)
    {
        Optional<User> existingUser = userRepository.findById(userId);
        existingUser.get().setLogin(user.getLogin());
        existingUser.get().setName(user.getName());
        existingUser.get().setSurname(user.getSurname());
        existingUser.get().setAge(user.getAge());
        existingUser.get().setRole(user.getRole());
        userRepository.save(existingUser.get());
    }

    public void deleteUserById(Integer userId)
    {
        List<SharedNote> list = sharedRepository.findRelatedNotes(userId);
        sharedRepository.deleteAll(list);
        userRepository.deleteById(userId);
    }

    public boolean userExists(User user)
    {
        return userRepository.findByLogin(user.getLogin()).isPresent();
    }
}

