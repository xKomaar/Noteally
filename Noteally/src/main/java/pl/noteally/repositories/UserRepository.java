package pl.noteally.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.noteally.data.User;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    @Override
    Optional<User> findById(Integer integer);

    Optional<User> findByLogin(String login);
}
