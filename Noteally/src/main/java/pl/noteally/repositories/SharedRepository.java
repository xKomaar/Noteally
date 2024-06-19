package pl.noteally.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.noteally.domain.SharedNote;

import java.util.List;
@Repository
public interface SharedRepository extends JpaRepository<SharedNote, Long> {

    @Query
            ("SELECT n FROM SharedNote n WHERE n.user.id = ?1")
    List<SharedNote> findByUserId(Long userId);

    @Query
            ("SELECT n FROM SharedNote n WHERE n.note.catalog.user.id = ?1")
    List<SharedNote> findMySharedNotes(Long userId);

    @Query
            ("SELECT n FROM SharedNote n WHERE n.user.id = ?1 OR n.note.catalog.user.id = ?1")
    List<SharedNote> findRelatedNotes(Long userId);
}
