package pl.noteally.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.noteally.data.Catalog;

import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog, Integer> {
    @Query
    ("SELECT c FROM Catalog c WHERE c.user.id = ?1")
    List<Catalog> findByUserId(int userId);

}