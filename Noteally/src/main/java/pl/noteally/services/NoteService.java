package pl.noteally.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.noteally.domain.Catalog;
import pl.noteally.domain.Note;
import pl.noteally.domain.SharedNote;
import pl.noteally.repositories.NoteRepository;
import pl.noteally.repositories.SharedRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NoteService {
    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;
    private final SharedRepository sharedRepository;
    private final CatalogService catalogService;
    private final EmailService emailService;

    public List<Note> getNotesByCatalogId(Long catalogId) {
        return noteRepository.findByCatalogId(catalogId);
    }

    public List<Note> getNotesFromSharedByCatalogId(Long userId) {
        List<SharedNote> sNotes = sharedRepository.findByUserId(userId);
        List<Note> notes = new ArrayList<>();
        for (SharedNote sNote : sNotes) {
            notes.add(sNote.getNote());
        }
        return notes;
    }

    public List<SharedNote> getMySharedNotes(Long userId) {
        return sharedRepository.findMySharedNotes(userId);
    }

    @Transactional
    public void saveNote(Note note, Long catalogId) {
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            note.setCatalog(catalog);
            note.setDate(LocalDate.now());
            note.setOwner(catalog.getUser().getEmail());
            noteRepository.save(note);
            logger.info("NoteService.saveNote(): Note with id: {} saved successfully", note.getId());
        } catch (EntityNotFoundException e) {
            String errorMsg = "Failed to save note: " + e.getMessage();
            logger.error("NoteService.saveNote(): {}", errorMsg);
            throw new EntityNotFoundException(errorMsg, e);
        }
    }

    @Transactional
    public void saveSharedNote(SharedNote sharedNote) {
        sharedRepository.save(sharedNote);
        String subject = "A note has been shared with you";
        String body = "A user with an email: " +
                sharedNote.getNote().getOwner() +
                " shared a note \"" + sharedNote.getNote().getTitle() + "\" with you!";
        try {
            emailService.sendEmail(sharedNote.getUser().getEmail(), subject, body);
            logger.info("NoteService.saveSharedNote(): Shared note saved and email sent to: {}", sharedNote.getUser().getEmail());
        } catch (MailSendException e) {
            String errorMsg = "Failed to send email to " + sharedNote.getUser().getEmail() + ": " + e.getMessage();
            logger.error("NoteService.saveSharedNote(): {}", errorMsg);
        }
    }
    @Transactional
    public void updateNote(Note note, Long noteId) {
        Note existingNote = noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    String errorMsg = "Note not found with id: " + noteId;
                    logger.error("NoteService.updateNote(): {}", errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setLink(note.getLink());
        noteRepository.save(existingNote);
        logger.info("NoteService.updateNote(): Note with id: {} updated successfully", noteId);
    }
    @Transactional
    public void deleteNoteById(Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            String errorMsg = "Note not found with id: " + noteId;
            logger.error("NoteService.deleteNoteById(): {}", errorMsg);
            throw new EntityNotFoundException(errorMsg);
        }
        noteRepository.deleteById(noteId);
        logger.info("NoteService.deleteNoteById(): Note with id: {} deleted successfully", noteId);
    }
    @Transactional
    public void deleteShareById(Long shareId) {
        if (!sharedRepository.existsById(shareId)) {
            String errorMsg = "Shared note not found with id: " + shareId;
            logger.error("NoteService.deleteShareById(): {}", errorMsg);
            throw new EntityNotFoundException(errorMsg);
        }
        sharedRepository.deleteById(shareId);
        logger.info("NoteService.deleteShareById(): Shared note with id: {} deleted successfully", shareId);
    }

    public Note getNoteById(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    String errorMsg = "Note not found with id: " + noteId;
                    logger.error("NoteService.getNoteById(): {}", errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });
    }

    public SharedNote getShareById(Long shareId) {
        return sharedRepository.findById(shareId)
                .orElseThrow(() -> {
                    String errorMsg = "Shared note not found with id: " + shareId;
                    logger.error("NoteService.getShareById(): {}", errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });
    }
}
