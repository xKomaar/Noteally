package pl.noteally.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.noteally.data.Catalog;
import pl.noteally.data.Note;
import pl.noteally.data.SharedNote;
import pl.noteally.repositories.NoteRepository;
import pl.noteally.repositories.SharedRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final SharedRepository sharedRepository;
    private final CatalogService catalogService;
    public List<Note> getNotesByCatalogId(Integer catalogId) {
        return noteRepository.findByCatalogId(catalogId);
    }

    public List<Note> getNotesFromSharedByCatalogId(Integer userId) {
        List<SharedNote> sNote = sharedRepository.findByUserId(userId);
        List<Note> notes = new ArrayList<>();
        for (int i=0; i< sNote.size(); i++)
            notes.add(sNote.get(i).getNote());
        return notes;
    }

    public List<SharedNote> getMySharedNotes(Integer userId) {
        return sharedRepository.findMySharedNotes(userId);
    }

    public void saveNote(Note note, Integer catalogId)
    {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        note.setCatalog(catalog.get());
        note.setDate(LocalDate.now());
        note.setOwner(catalog.get().getUser().getLogin());
        noteRepository.save(note);
    }

    public void saveSharedNote(SharedNote sharedNote)
    {
        sharedRepository.save(sharedNote);
    }

    public void updateNote(Note note, Integer noteId)
    {
        Note existingNote = noteRepository.getById(noteId);
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setLink(note.getLink());
        noteRepository.save(existingNote);
    }

    public void deleteNoteById(Integer noteId)
    {
        noteRepository.deleteById(noteId);
    }

    public void deleteShareById(Integer shareId) {sharedRepository.deleteById(shareId);}

    public Optional<Note> getNoteById(Integer noteId){
        return noteRepository.findById(noteId);
    }

    public Optional<SharedNote> getShareById(Integer shareId){
        return sharedRepository.findById(shareId);
    }
}
