package pl.noteally;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.noteally.domain.Catalog;
import pl.noteally.domain.Note;
import pl.noteally.domain.SharedNote;
import pl.noteally.domain._User;
import pl.noteally.repositories.NoteRepository;
import pl.noteally.repositories.SharedRepository;
import pl.noteally.repositories.UserRepository;
import pl.noteally.services.NoteService;
import pl.noteally.services.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private SharedRepository sharedRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private Catalog defaultCatalog;
    private _User testUser;
    private _User anotherUser;

    @BeforeEach
    public void setUp() {
        noteRepository.deleteAll();
        sharedRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new _User();
        testUser.setEmail("user@example.com");
        testUser.setPassword("password");
        testUser.setName("Jakub");
        testUser.setSurname("Komarewski");
        testUser.setAge(22);
        userService.signUpUser(testUser);

        anotherUser = new _User();
        anotherUser.setEmail("anotheruser@example.com");
        anotherUser.setPassword("password");
        anotherUser.setName("Another");
        anotherUser.setSurname("User");
        anotherUser.setAge(30);
        userService.signUpUser(anotherUser);

        defaultCatalog = userService.getUserById(testUser.getId()).getCatalogs().get(0);
    }

    @Test
    public void testSaveNote_Success() {
        Note note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note");

        assertDoesNotThrow(() -> noteService.saveNote(note, defaultCatalog.getId()));

        assertNotNull(note.getId());
        assertNotNull(note.getDate());
        assertEquals(LocalDate.now(), note.getDate());
        assertEquals(defaultCatalog.getId(), note.getCatalog().getId());
        assertEquals(testUser.getEmail(), note.getOwner());
    }

    @Test
    public void testSaveNote_CatalogNotFound() {
        Note note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note");

        assertThrows(EntityNotFoundException.class, () -> noteService.saveNote(note, 999L));
    }

    @Test
    public void testSaveSharedNote_Success() {
        Note note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note");
        note.setCatalog(defaultCatalog);
        note = noteRepository.save(note);

        SharedNote sharedNote = new SharedNote();
        sharedNote.setNote(note);
        sharedNote.setUser(anotherUser);

        assertDoesNotThrow(() -> noteService.saveSharedNote(sharedNote));

        SharedNote foundSharedNote = sharedRepository.findById(sharedNote.getId())
                .orElseThrow(() -> new EntityNotFoundException("SharedNote not found"));

        assertNotNull(foundSharedNote);
        assertEquals(sharedNote.getNote().getId(), foundSharedNote.getNote().getId());
        assertEquals(sharedNote.getUser().getId(), foundSharedNote.getUser().getId());

        // Check if the shared note appears in the shared catalog of another user
        final Note finalNote = note; // Making note effectively final
        List<Note> sharedNotes = noteService.getNotesFromSharedByCatalogId(anotherUser.getId());
        assertTrue(sharedNotes.stream().anyMatch(n -> n.getId().equals(finalNote.getId())));
    }

    @Test
    public void testUpdateNote_Success() {
        Note note = new Note();
        note.setTitle("Old Title");
        note.setContent("Old Content");
        note.setCatalog(defaultCatalog);
        note = noteRepository.save(note);

        Note updatedNote = new Note();
        updatedNote.setId(note.getId());
        updatedNote.setTitle("Updated Title");
        updatedNote.setContent("Updated Content");

        final Long noteId = note.getId(); // Making noteId effectively final
        assertDoesNotThrow(() -> noteService.updateNote(updatedNote, noteId));

        Note foundNote = noteService.getNoteById(noteId);

        assertEquals(updatedNote.getTitle(), foundNote.getTitle());
        assertEquals(updatedNote.getContent(), foundNote.getContent());
    }

    @Test
    public void testDeleteNoteById_Success() {
        Note note = new Note();
        note.setTitle("To delete");
        note.setContent("This is a note to be deleted");
        note.setCatalog(defaultCatalog);
        note = noteRepository.save(note);

        final Long noteId = note.getId();
        assertDoesNotThrow(() -> noteService.deleteNoteById(noteId));

        assertThrows(EntityNotFoundException.class, () -> noteService.getNoteById(noteId));
    }

    @Test
    public void testGetNoteById_Success() {
        Note note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note");
        note.setCatalog(defaultCatalog);
        note = noteRepository.save(note);

        final Long noteId = note.getId(); // Making noteId effectively final
        Note foundNote = noteService.getNoteById(noteId);

        assertNotNull(foundNote);
        assertEquals(noteId, foundNote.getId());
        assertEquals(note.getTitle(), foundNote.getTitle());
        assertEquals(note.getContent(), foundNote.getContent());
    }

    @Test
    public void testGetNoteById_NoteNotFound() {
        assertThrows(EntityNotFoundException.class, () -> noteService.getNoteById(999L));
    }

    @Test
    public void testGetNotesByCatalogId() {
        createNoteInCatalog(defaultCatalog, "Note 1", "Content for Note 1");
        createNoteInCatalog(defaultCatalog, "Note 2", "Content for Note 2");

        List<Note> notes = noteService.getNotesByCatalogId(defaultCatalog.getId());

        assertEquals(2, notes.size());
    }

    @Test
    public void testGetNotesFromSharedByCatalogId() {
        Note note = createNoteInCatalog(defaultCatalog, "Shared Note", "Content for Shared Note");
        SharedNote sharedNote = new SharedNote();
        sharedNote.setNote(note);
        sharedNote.setUser(anotherUser);
        sharedRepository.save(sharedNote);

        List<Note> sharedNotes = noteService.getNotesFromSharedByCatalogId(anotherUser.getId());

        assertEquals(1, sharedNotes.size());
        assertEquals(note.getId(), sharedNotes.get(0).getId());
    }

    @Test
    public void testGetMySharedNotes() {
        Note note = createNoteInCatalog(defaultCatalog, "Shared Note", "Content for Shared Note");
        SharedNote sharedNote = new SharedNote();
        sharedNote.setNote(note);
        sharedNote.setUser(testUser);
        sharedRepository.save(sharedNote);

        List<SharedNote> mySharedNotes = noteService.getMySharedNotes(testUser.getId());

        assertEquals(1, mySharedNotes.size());
        assertEquals(note.getId(), mySharedNotes.get(0).getNote().getId());
    }

    @Test
    public void testDeleteShareById_Success() {
        Note note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note");
        note.setCatalog(defaultCatalog);
        note = noteRepository.save(note);

        SharedNote sharedNote = new SharedNote();
        sharedNote.setNote(note);
        sharedNote.setUser(anotherUser);
        sharedNote = sharedRepository.save(sharedNote);

        final Long shareId = sharedNote.getId(); // Making shareId effectively final
        assertDoesNotThrow(() -> noteService.deleteShareById(shareId));

        assertThrows(EntityNotFoundException.class, () -> noteService.getShareById(shareId));
    }

    @Test
    public void testGetShareById_Success() {
        Note note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note");
        note.setCatalog(defaultCatalog);
        note = noteRepository.save(note);

        SharedNote sharedNote = new SharedNote();
        sharedNote.setNote(note);
        sharedNote.setUser(anotherUser);
        sharedNote = sharedRepository.save(sharedNote);

        final Long shareId = sharedNote.getId();
        SharedNote foundSharedNote = noteService.getShareById(shareId);

        assertNotNull(foundSharedNote);
        assertEquals(shareId, foundSharedNote.getId());
        assertEquals(sharedNote.getNote().getId(), foundSharedNote.getNote().getId());
    }

    @Test
    public void testGetShareById_SharedNoteNotFound() {
        assertThrows(EntityNotFoundException.class, () -> noteService.getShareById(999L));
    }

    private Note createNoteInCatalog(Catalog catalog, String title, String content) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setCatalog(catalog);
        return noteRepository.save(note);
    }
}
