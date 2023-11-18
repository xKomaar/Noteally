package pl.noteally.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.noteally.data.Catalog;
import pl.noteally.data.Note;
import pl.noteally.data.SharedNote;
import pl.noteally.data.User;
import pl.noteally.services.CatalogService;
import pl.noteally.services.NoteService;
import pl.noteally.services.UserService;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/catalogs/{catalogId}")
@AllArgsConstructor
public class NotesController {
    final private NoteService noteService;
    final private CatalogService catalogService;
    final private UserService userService;

    @GetMapping("")
    public String getNotesByCatalogId(Model model, @PathVariable("catalogId") Integer catalogId, HttpSession session, HttpServletRequest request) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");

        Optional<String> sortValue = Arrays.stream(request.getCookies()).filter(
                c -> c.getName().equals("noteCookie" + session.getAttribute("userId"))).map(Cookie::getValue).findAny();

        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId)) {
            if (sortValue.get().equals("ASC"))
                return "redirect:/catalogs/{catalogId}/ASC";
            else if (sortValue.get().equals("DESC"))
                return "redirect:/catalogs/{catalogId}/DESC";
            else if (sortValue.get().equals("dataASC"))
                return "redirect:/catalogs/{catalogId}/dataASC";
            else if (sortValue.get().equals("dataDESC"))
                return "redirect:/catalogs/{catalogId}/dataDESC";

            List<Note> noteList;
            if (catalog.get().getName().equals("shared")) {
                noteList = noteService.getNotesFromSharedByCatalogId(userId);
            } else {
                noteList = noteService.getNotesByCatalogId(catalogId);
            }

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

            Optional<User> user = userService.getUserById((Integer) session.getAttribute("userId"));
            model.addAttribute("oldDate", oldDate);
            model.addAttribute("newDate", newDate);
            model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
            model.addAttribute("catalog", catalog.get());
            model.addAttribute("notes", noteList);
            model.addAttribute("user", user.get());
            return "notes";
        }
        return "redirect:/catalogs";
    }

    @GetMapping("/ASC")
    public String sortNotesByTitleASC(Model model, @PathVariable("catalogId") Integer catalogId,
                                      HttpSession session, HttpServletResponse response, HttpServletRequest request) {

        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");

        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId)) {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(
                    c -> c.getName().equals("noteCookie" + session.getAttribute("userId"))).findAny();
            cookie.get().setValue("ASC");
            response.addCookie(cookie.get());

            List<Note> noteList;
            if (catalog.get().getName().equals("shared")) {
                noteList = noteService.getNotesFromSharedByCatalogId(userId);
            } else {
                noteList = noteService.getNotesByCatalogId(catalogId);
            }
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

            Optional<User> user = userService.getUserById((Integer) session.getAttribute("userId"));
            noteList.sort(Comparator.comparing(Note::getTitle));
            model.addAttribute("oldDate", oldDate);
            model.addAttribute("newDate", newDate);
            model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
            model.addAttribute("catalog", catalog.get());
            model.addAttribute("notes", noteList);
            model.addAttribute("user", user.get());
            return "notes";
        }
        return "redirect:/catalogs";
    }

    @GetMapping("/DESC")
    public String sortNotesByTitleDESC(Model model, @PathVariable("catalogId") Integer catalogId,
                                       HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");

        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId)) {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(
                    c -> c.getName().equals("noteCookie" + session.getAttribute("userId"))).findAny();
            cookie.get().setValue("DESC");
            response.addCookie(cookie.get());

            List<Note> noteList;
            if (catalog.get().getName().equals("shared")) {
                noteList = noteService.getNotesFromSharedByCatalogId(userId);
            } else {
                noteList = noteService.getNotesByCatalogId(catalogId);
            }

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

            Optional<User> user = userService.getUserById((Integer) session.getAttribute("userId"));
            noteList.sort(Comparator.comparing(Note::getTitle).reversed());
            model.addAttribute("oldDate", oldDate);
            model.addAttribute("newDate", newDate);
            model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
            model.addAttribute("catalog", catalog.get());
            model.addAttribute("notes", noteList);
            model.addAttribute("user", user.get());
            return "notes";
        }
        return "redirect:/catalogs";
    }

    @GetMapping("/dataASC")
    public String sortNotesByDateASC(Model model, @PathVariable("catalogId") Integer catalogId,
                                     HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");

        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId)) {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(
                    c -> c.getName().equals("noteCookie" + session.getAttribute("userId"))).findAny();
            cookie.get().setValue("dataASC");
            response.addCookie(cookie.get());
            List<Note> noteList;
            if (catalog.get().getName().equals("shared")) {
                noteList = noteService.getNotesFromSharedByCatalogId(userId);
            } else {
                noteList = noteService.getNotesByCatalogId(catalogId);
            }
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
            Optional<User> user = userService.getUserById((Integer) session.getAttribute("userId"));
            noteList.sort(Comparator.comparing(Note::getDate));
            model.addAttribute("oldDate", oldDate);
            model.addAttribute("newDate", newDate);
            model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
            model.addAttribute("catalog", catalog.get());
            model.addAttribute("notes", noteList);
            model.addAttribute("user", user.get());
            return "notes";
        }
        return "redirect:/catalogs";
    }

    @GetMapping("/dataDESC")
    public String sortNotesByDateDESC(Model model, @PathVariable("catalogId") Integer catalogId,
                                      HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");

        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId)) {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(
                    c -> c.getName().equals("noteCookie" + session.getAttribute("userId"))).findAny();
            cookie.get().setValue("dataDESC");
            response.addCookie(cookie.get());

            List<Note> noteList;
            if (catalog.get().getName().equals("shared")) {
                noteList = noteService.getNotesFromSharedByCatalogId(userId);
            } else {
                noteList = noteService.getNotesByCatalogId(catalogId);
            }

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

            Optional<User> user = userService.getUserById((Integer) session.getAttribute("userId"));
            noteList.sort(Comparator.comparing(Note::getDate).reversed());
            model.addAttribute("oldDate", oldDate);
            model.addAttribute("newDate", newDate);
            model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
            model.addAttribute("catalog", catalog.get());
            model.addAttribute("notes", noteList);
            model.addAttribute("user", user.get());
            return "notes";
        }
        return "redirect:/catalogs";
    }

    @GetMapping("/deleteFilters")
    public String deleteFilters(Model model, @PathVariable("catalogId") Integer catalogId,
                                HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");

        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId)) {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(
                    c -> c.getName().equals("noteCookie" + session.getAttribute("userId"))).findAny();
            cookie.get().setValue("default");
            response.addCookie(cookie.get());
            return "redirect:/catalogs/{catalogId}";
        }
        return "redirect:/catalogs";
    }


    @GetMapping("/createNote")
    public String redirectCreate(Model model, @PathVariable("catalogId") Integer catalogId, HttpSession session) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");
        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId) && !(catalog.get().getName().equals("shared"))) {
            Note note = new Note();
            model.addAttribute("note", note);
            model.addAttribute("catalog", catalog.get());
            return "createNote";
        }
        return "redirect:/catalogs";
    }

    @PostMapping("/createNote")
    public String addNote(@Valid @ModelAttribute("note") Note note, BindingResult bindingResult, Model model, @PathVariable("catalogId") Integer catalogId) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors",bindingResult);
            Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
            model.addAttribute("catalog", catalog.get());
            return "createNote";
        }

        noteService.saveNote(note, catalogId);
        return "redirect:/catalogs/" + catalogId;
    }

    @GetMapping("/deleteNote/{noteId}")
    public String delete(Model model, @PathVariable("noteId") Integer noteId, @PathVariable("catalogId") Integer catalogId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId) && !(catalog.get().getName().equals("shared"))) {
            noteService.deleteNoteById(noteId);
            return "redirect:/catalogs/" + catalogId;
        }
        return "redirect:/catalogs";
    }

    @GetMapping("/editNote/{noteId}")
    public String redirectEdit(Model model, @PathVariable("noteId") Integer noteId, @PathVariable("catalogId") Integer catalogId, HttpSession session) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");
        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId) && !(catalog.get().getName().equals("shared"))) {
            Optional<Note> note = noteService.getNoteById(noteId);
            model.addAttribute("note", note.get());
            model.addAttribute("catalog", catalog.get());
            return "editNote";
        }
        return "redirect:/catalogs";
    }

    @PostMapping("/editNote/{noteId}")
    public String editNote(@Valid @ModelAttribute("note") Note note, BindingResult bindingResult, @PathVariable("noteId") Integer noteId, @PathVariable("catalogId") Integer catalogId, Model model) {
        if (bindingResult.hasErrors()) {
            note.setId(noteId);
            model.addAttribute("note", note);
            model.addAttribute("errors",bindingResult);
            Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
            model.addAttribute("catalog", catalog.get());
            return "editNote";
        }

        noteService.updateNote(note, noteId);
        return "redirect:/catalogs/" + catalogId;
    }

    @GetMapping("/shareNote/{noteId}")
    public String redirectShare(Model model, @PathVariable("noteId") Integer noteId, @PathVariable("catalogId") Integer catalogId, HttpSession session) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");
        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId) && !(catalog.get().getName().equals("shared"))) {
            List<User> userList = userService.getUsers();
            List<User> filteredUserList = new ArrayList<>();
            Optional<Note> note = noteService.getNoteById(noteId);
            for (User user : userList) {
                if (!user.getId().equals(session.getAttribute("userId"))) {
                    filteredUserList.add(user);
                }
            }
            model.addAttribute("users", filteredUserList);
            model.addAttribute("note", note.get());
            model.addAttribute("catalog", catalog.get());
            return "shareNote";
        }
        return "redirect:/catalogs";
    }

    @PostMapping("/shareNote/{noteId}")
    public String shareNote(@RequestParam(value = "username") String username, @PathVariable("noteId") Integer noteId, @PathVariable("catalogId") Integer catalogId, HttpSession session) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent() && !session.getAttribute("userId").equals(user.get().getId())) {
            SharedNote sharedNote = new SharedNote();
            sharedNote.setNote(noteService.getNoteById(noteId).get());
            sharedNote.setUser(user.get());
            noteService.saveSharedNote(sharedNote);
        }
        return "redirect:/catalogs/" + catalogId;
    }

    @GetMapping("/deleteSharedNote/{shareId}")
    public String deleteSharedNote(Model model, @PathVariable("shareId") Integer shareId, @PathVariable("catalogId") Integer catalogId, HttpSession session) {
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");
        Optional<SharedNote> share = noteService.getShareById(shareId);
        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId) && share.isPresent() && share.get().getNote().getCatalog().getUser().getId().equals(userId) && !(catalog.get().getName().equals("shared"))) {

            noteService.deleteShareById(shareId);
            return "redirect:/catalogs/" + catalogId;
        }
        return "redirect:/catalogs";
    }

    @PostMapping("/search")
    public String search(@RequestParam(name="search") String search, Model model, @PathVariable("catalogId") Integer catalogId, HttpSession session){

        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");

        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId)) {
            List<Note> noteList;
            if (catalog.get().getName().equals("shared")) {
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

            Optional<User> user = userService.getUserById((Integer) session.getAttribute("userId"));
            model.addAttribute("oldDate", oldDate);
            model.addAttribute("newDate", newDate);
            model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
            model.addAttribute("catalog", catalog.get());
            model.addAttribute("notes", noteList);
            model.addAttribute("user", user.get());
            return "notes";
        }
        return "redirect:/catalogs";
    }

    @PostMapping("/filterByDates")
    public String filter(@RequestParam(name="startDate") LocalDate startDate,
                         @RequestParam(name="endDate") LocalDate endDate, Model model, @PathVariable("catalogId") Integer catalogId, HttpSession session){

        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        Integer userId = (Integer) session.getAttribute("userId");
        if (catalog.isPresent() && catalog.get().getUser().getId().equals(userId)) {
            List<Note> noteList;
            if (catalog.get().getName().equals("shared")) {
                noteList = noteService.getNotesFromSharedByCatalogId(userId);
            } else {
                noteList = noteService.getNotesByCatalogId(catalogId);
            }
            Optional<User> user = userService.getUserById((Integer) session.getAttribute("userId"));
            model.addAttribute("oldDate", startDate);
            model.addAttribute("newDate", endDate);
            model.addAttribute("user", user.get());
            model.addAttribute("sharedNotes", noteService.getMySharedNotes(userId));
            model.addAttribute("catalog", catalog.get());model.addAttribute("notes", noteList.stream().filter(n
                    -> (n.getDate().isAfter(startDate) && n.getDate().isBefore(endDate))
                        || n.getDate().isEqual(startDate) || n.getDate().isEqual(endDate)).toList());
            return "notes";
        }
        return "redirect:/catalogs";
    }
}