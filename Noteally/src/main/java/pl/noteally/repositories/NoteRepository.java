package pl.noteally.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.noteally.data.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    @Query
    ("SELECT n FROM Note n WHERE n.catalog.id = ?1")
    List<Note> findByCatalogId(int catalogId);
}
