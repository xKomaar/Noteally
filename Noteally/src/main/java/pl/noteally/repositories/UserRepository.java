package pl.noteally.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.noteally.domain._User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<_User, Long> {
    @Query
            ("SELECT u FROM _User u WHERE u.email = ?1")
    Optional<_User> findByEmail(String email);
}
