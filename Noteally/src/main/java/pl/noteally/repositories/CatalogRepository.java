package pl.noteally.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.noteally.domain.Catalog;

import java.util.List;
@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    @Query
    ("SELECT c FROM Catalog c WHERE c.user.id = ?1")
    List<Catalog> findByUserId(Long userId);

}