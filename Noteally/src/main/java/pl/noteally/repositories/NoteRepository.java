package pl.noteally.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.noteally.domain.Note;
import pl.noteally.domain._User;

import java.util.List;
import java.util.Optional;
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query
    ("SELECT n FROM Note n WHERE n.catalog.id = ?1")
    List<Note> findByCatalogId(Long catalogId);
}
