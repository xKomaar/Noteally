package pl.noteally.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.noteally.domain.Catalog;
import pl.noteally.domain.Note;
import pl.noteally.domain.SharedNote;
import pl.noteally.domain._User;
import pl.noteally.services.CatalogService;
import pl.noteally.services.NoteService;
import pl.noteally.services.UserService;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/catalogs/{catalogId}")
@AllArgsConstructor
public class NotesController {

    private static final Logger logger = LoggerFactory.getLogger(NotesController.class);

    final private NoteService noteService;
    final private CatalogService catalogService;
    final private UserService userService;

    @GetMapping("")
    public String getNotesByCatalogId(Model model, @PathVariable("catalogId") Long catalogId, HttpSession session, HttpServletRequest request) {
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Long userId = (Long) session.getAttribute("userId");

            Optional<String> sortValue = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("noteCookie" + userId))
                    .map(Cookie::getValue)
                    .findAny();

            if (catalog.getUser().getId().equals(userId)) {
                if (sortValue.isPresent()) {
                    switch (sortValue.get()) {
                        case "ASC":
                            return "redirect:/catalogs/{catalogId}/ASC";
                        case "DESC":
                            return "redirect:/catalogs/{catalogId}/DESC";
                        case "dataASC":
                            return "redirect:/catalogs/{catalogId}/dataASC";
                        case "dataDESC":
                            return "redirect:/catalogs/{catalogId}/dataDESC";
                    }
                }

                List<Note> noteList = catalog.getName().equals("shared") ?
                        noteService.getNotesFromSharedByCatalogId(userId) :
                        noteService.getNotesByCatalogId(catalogId);

                LocalDate oldDate = noteList.stream().map(Note::getDate).min(LocalDate::compareTo).orElse(LocalDate.now());
                LocalDate newDate = noteList.stream().map(Note::getDate).max(LocalDate::compareTo).orElse(LocalDate.now());

                _User user = userService.getUserById(userId);
                model.addAttribute("oldDate", oldDate);
                model.addAttribute("newDate", newDate);
                model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
                model.addAttribute("catalog", catalog);
                model.addAttribute("notes", noteList);
                model.addAttribute("user", user);
                return "notes";
            }
            return "redirect:/catalogs";
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.getNotesByCatalogId(): Catalog not found: {}", e.getMessage());
            return "redirect:/catalogs";
        }
    }

    @GetMapping("/ASC")
    public String sortNotesByTitleASC(Model model, @PathVariable("catalogId") Long catalogId,
                                      HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        return sortNotes(model, catalogId, session, response, request, Comparator.comparing(Note::getTitle), "ASC");
    }

    @GetMapping("/DESC")
    public String sortNotesByTitleDESC(Model model, @PathVariable("catalogId") Long catalogId,
                                       HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        return sortNotes(model, catalogId, session, response, request, Comparator.comparing(Note::getTitle).reversed(), "DESC");
    }

    @GetMapping("/dataASC")
    public String sortNotesByDateASC(Model model, @PathVariable("catalogId") Long catalogId,
                                     HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        return sortNotes(model, catalogId, session, response, request, Comparator.comparing(Note::getDate), "dataASC");
    }

    @GetMapping("/dataDESC")
    public String sortNotesByDateDESC(Model model, @PathVariable("catalogId") Long catalogId,
                                      HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        return sortNotes(model, catalogId, session, response, request, Comparator.comparing(Note::getDate).reversed(), "dataDESC");
    }

    private String sortNotes(Model model, Long catalogId, HttpSession session, HttpServletResponse response, HttpServletRequest request,
                             Comparator<Note> comparator, String sortType) {
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Long userId = (Long) session.getAttribute("userId");

            if (catalog.getUser().getId().equals(userId)) {
                Cookie cookie = Arrays.stream(request.getCookies())
                        .filter(c -> c.getName().equals("noteCookie" + userId))
                        .findFirst()
                        .orElse(new Cookie("noteCookie" + userId, sortType));
                cookie.setValue(sortType);
                response.addCookie(cookie);

                List<Note> noteList = catalog.getName().equals("shared") ?
                        noteService.getNotesFromSharedByCatalogId(userId) :
                        noteService.getNotesByCatalogId(catalogId);

                LocalDate oldDate = noteList.stream().map(Note::getDate).min(LocalDate::compareTo).orElse(LocalDate.now());
                LocalDate newDate = noteList.stream().map(Note::getDate).max(LocalDate::compareTo).orElse(LocalDate.now());

                _User user = userService.getUserById(userId);
                noteList.sort(comparator);
                model.addAttribute("oldDate", oldDate);
                model.addAttribute("newDate", newDate);
                model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
                model.addAttribute("catalog", catalog);
                model.addAttribute("notes", noteList);
                model.addAttribute("user", user);
                return "notes";
            }
            return "redirect:/catalogs";
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.sortNotes(): Catalog not found: {}", e.getMessage());
            return "redirect:/catalogs";
        }
    }

    @GetMapping("/deleteFilters")
    public String deleteFilters(@PathVariable("catalogId") Long catalogId, HttpSession session, HttpServletResponse response) {
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Long userId = (Long) session.getAttribute("userId");

            if (catalog.getUser().getId().equals(userId)) {
                Cookie cookie = new Cookie("noteCookie" + userId, "default");
                response.addCookie(cookie);
                return "redirect:/catalogs/{catalogId}";
            }
            return "redirect:/catalogs";
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.deleteFilters(): Catalog not found: {}", e.getMessage());
            return "redirect:/catalogs";
        }
    }

    @GetMapping("/createNote")
    public String redirectCreate(Model model, @PathVariable("catalogId") Long catalogId, HttpSession session) {
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Long userId = (Long) session.getAttribute("userId");
            if (catalog.getUser().getId().equals(userId) && !catalog.getName().equals("shared")) {
                Note note = new Note();
                model.addAttribute("note", note);
                model.addAttribute("catalog", catalog);
                return "createNote";
            }
            return "redirect:/catalogs";
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.redirectCreate(): Catalog not found: {}", e.getMessage());
            return "redirect:/catalogs";
        }
    }

    @PostMapping("/createNote")
    public String addNote(@Valid @ModelAttribute("note") Note note, BindingResult bindingResult, Model model, @PathVariable("catalogId") Long catalogId) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            try {
                Catalog catalog = catalogService.getCatalogById(catalogId);
                model.addAttribute("catalog", catalog);
            } catch (EntityNotFoundException e) {
                logger.error("NotesController.addNote(): Catalog not found: {}", e.getMessage());
                return "redirect:/catalogs";
            }
            return "createNote";
        }

        try {
            noteService.saveNote(note, catalogId);
            return "redirect:/catalogs/" + catalogId;
        } catch (RuntimeException e) {
            logger.error("NotesController.addNote(): Error saving note: {}", e.getMessage());
            return "redirect:/catalogs/" + catalogId;
        }
    }

    @GetMapping("/deleteNote/{noteId}")
    public String deleteNote(@PathVariable("noteId") Long noteId, @PathVariable("catalogId") Long catalogId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            Catalog catalog = catalogService.getCatalogById(catalogId);
            if (catalog.getUser().getId().equals(userId) && !catalog.getName().equals("shared")) {
                noteService.deleteNoteById(noteId);
            }
            return "redirect:/catalogs/" + catalogId;
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.deleteNote(): Catalog not found: {}", e.getMessage());
            return "redirect:/catalogs";
        } catch (RuntimeException e) {
            logger.error("NotesController.deleteNote(): Error deleting note: {}", e.getMessage());
            return "redirect:/catalogs/" + catalogId;
        }
    }

    @GetMapping("/editNote/{noteId}")
    public String redirectEdit(Model model, @PathVariable("catalogId") Long catalogId, @PathVariable("noteId") Long noteId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            Catalog catalog = catalogService.getCatalogById(catalogId);
            if (catalog.getUser().getId().equals(userId) && !catalog.getName().equals("shared")) {
                Note note = noteService.getNoteById(noteId);
                model.addAttribute("note", note);
                model.addAttribute("catalog", catalog);
                return "editNote";
            }
            return "redirect:/catalogs";
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.redirectEdit(): Catalog or Note not found: {}", e.getMessage());
            return "redirect:/catalogs";
        }
    }

    @PostMapping("/editNote/{noteId}")
    public String editNote(@Valid @ModelAttribute("note") Note note, BindingResult bindingResult, Model model,
                             @PathVariable("catalogId") Long catalogId, @PathVariable("noteId") Long noteId) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            try {
                Catalog catalog = catalogService.getCatalogById(catalogId);
                model.addAttribute("catalog", catalog);
            } catch (EntityNotFoundException e) {
                logger.error("NotesController.editNote(): Catalog not found: {}", e.getMessage());
                return "redirect:/catalogs";
            }
            return "editNote";
        }

        try {
            noteService.updateNote(note, noteId);
            return "redirect:/catalogs/" + catalogId;
        } catch (RuntimeException e) {
            logger.error("NotesController.editNote(): Error updating note: {}", e.getMessage());
            return "redirect:/catalogs/" + catalogId;
        }
    }

    @GetMapping("/shareNote/{noteId}")
    public String redirectShare(@PathVariable("noteId") Long noteId, @PathVariable("catalogId") Long catalogId, HttpSession session, Model model) {
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Long userId = (Long) session.getAttribute("userId");
            if (catalog.getUser().getId().equals(userId) && !(catalog.getName().equals("shared"))) {
                List<_User> userList = userService.getUsers();
                List<_User> filteredUserList = new ArrayList<>();
                Note note = noteService.getNoteById(noteId);
                for (_User user : userList) {
                    if (!user.getId().equals(session.getAttribute("userId"))) {
                        filteredUserList.add(user);
                    }
                }
                model.addAttribute("users", filteredUserList);
                model.addAttribute("note", note);
                model.addAttribute("catalog", catalog);
                return "shareNote";
            }
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.redirectShare(): Note not found: {}", e.getMessage());
            return "redirect:/catalogs";
        }
        return "redirect:/catalogs";
    }

    @PostMapping("/shareNote/{noteId}")
    public String shareNote(@RequestParam(value = "email") String email, @PathVariable("noteId") Long noteId, @PathVariable("catalogId") Long catalogId, HttpSession session) {
        _User user;
        try {
            user = userService.getUserByEmail(email);
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.shareNote(): User not found: {}", e.getMessage());
            return "redirect:/catalogs/" + catalogId;
        }
        if (!session.getAttribute("userId").equals(user.getId())) {
            SharedNote sharedNote = new SharedNote();
            sharedNote.setNote(noteService.getNoteById(noteId));
            sharedNote.setUser(user);
            noteService.saveSharedNote(sharedNote);
        }
        return "redirect:/catalogs/" + catalogId;
    }

    @GetMapping("/deleteSharedNote/{shareId}")
    public String deleteSharedNote(@PathVariable("shareId") Long shareId, @PathVariable("catalogId") Long catalogId, HttpSession session) {
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Long userId = (Long) session.getAttribute("userId");
            SharedNote share = noteService.getShareById(shareId);
            if (catalog.getUser().getId().equals(userId) && share.getNote().getCatalog().getUser().getId().equals(userId) && !(catalog.getName().equals("shared"))) {
                noteService.deleteShareById(shareId);
                return "redirect:/catalogs/" + catalogId;
            }
            return "redirect:/catalogs";
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.deleteSharedNote(): Entity not found: {}", e.getMessage());
            return "redirect:/catalogs";
        } catch (RuntimeException e) {
            logger.error("NotesController.deleteSharedNote(): Error deleting shared note: {}", e.getMessage());
            return "redirect:/catalogs";
        }
    }

    @PostMapping("/search")
    public String search(@RequestParam(name="search") String search, Model model, @PathVariable("catalogId") Long catalogId, HttpSession session){
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Long userId = (Long) session.getAttribute("userId");

            if (catalog.getUser().getId().equals(userId)) {
                List<Note> noteList;
                if (catalog.getName().equals("shared")) {
                    noteList = noteService.getNotesFromSharedByCatalogId(userId);
                } else {
                    noteList = noteService.getNotesByCatalogId(catalogId);
                }

                noteList = noteList.stream().filter(n -> n.getTitle().contains(search)).toList();

                LocalDate oldDate = null;
                LocalDate newDate = null;

                for (Note n : noteList) {
                    LocalDate currentDate = n.getDate();

                    if (oldDate == null || currentDate.isBefore(oldDate)) {
                        oldDate = currentDate;
                    }

                    if (newDate == null || currentDate.isAfter(newDate)) {
                        newDate = currentDate;
                    }
                }

                if(oldDate == null && newDate == null){
                    oldDate = LocalDate.now();
                    newDate = LocalDate.now();
                }

                _User user = userService.getUserById(userId);
                model.addAttribute("oldDate", oldDate);
                model.addAttribute("newDate", newDate);
                model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
                model.addAttribute("catalog", catalog);
                model.addAttribute("notes", noteList);
                model.addAttribute("user", user);
                return "notes";
            }
            return "redirect:/catalogs";
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.search(): Entity not found: {}", e.getMessage());
            return "redirect:/catalogs";
        } catch (RuntimeException e) {
            logger.error("NotesController.search(): Error during search: {}", e.getMessage());
            return "redirect:/catalogs";
        }
    }

    @PostMapping("/filterByDates")
    public String filter(@RequestParam(name="startDate") LocalDate startDate,
                         @RequestParam(name="endDate") LocalDate endDate, Model model, @PathVariable("catalogId") Long catalogId, HttpSession session){
        try {
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Long userId = (Long) session.getAttribute("userId");
            if (catalog.getUser().getId().equals(userId)) {
                List<Note> noteList;
                if (catalog.getName().equals("shared")) {
                    noteList = noteService.getNotesFromSharedByCatalogId(userId);
                } else {
                    noteList = noteService.getNotesByCatalogId(catalogId);
                }
                _User user = userService.getUserById(userId);
                model.addAttribute("oldDate", startDate);
                model.addAttribute("newDate", endDate);
                model.addAttribute("user", user);
                model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
                model.addAttribute("catalog", catalog);
                model.addAttribute("notes", noteList.stream().filter(n
                        -> (n.getDate().isAfter(startDate) && n.getDate().isBefore(endDate))
                        || n.getDate().isEqual(startDate) || n.getDate().isEqual(endDate)).toList());
                return "notes";
            }
            return "redirect:/catalogs";
        } catch (EntityNotFoundException e) {
            logger.error("NotesController.filter(): Entity not found: {}", e.getMessage());
            return "redirect:/catalogs";
        } catch (RuntimeException e) {
            logger.error("NotesController.filter(): Error filtering notes by date: {}", e.getMessage());
            return "redirect:/catalogs";
        }
    }
}