package pl.noteally.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.noteally.domain.Catalog;
import pl.noteally.domain._User;
import pl.noteally.services.CatalogService;
import pl.noteally.services.UserService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/catalogs")
@AllArgsConstructor
public class CatalogsController {
    private final CatalogService catalogService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(CatalogsController.class);

    @GetMapping("")
    public String getCatalogsByUserId(Model model, HttpSession session, HttpServletRequest request) {
        try {
            Optional<String> sortValue = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("catalogCookie" + session.getAttribute("userId")))
                    .map(Cookie::getValue)
                    .findAny();

            if (sortValue.isEmpty())
                return "redirect:/login";

            String redirectPath = determineRedirectPath(sortValue.get());
            if (redirectPath != null)
                return redirectPath;

            List<Catalog> catalogList = catalogService.getCatalogsByUserId((Long) session.getAttribute("userId"));
            _User user = userService.getUserById((Long) session.getAttribute("userId"));
            model.addAttribute("catalogs", catalogList);
            model.addAttribute("user", user);
            logger.info("CatalogsController.getCatalogsByUserId(): Fetched {} catalogs for user ID: {}", catalogList.size(), session.getAttribute("userId"));
            return "catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.getCatalogsByUserId(): Error fetching catalogs: {}", e.getMessage());
            return "error";
        }
    }

    private String determineRedirectPath(String sortValue) {
        switch (sortValue) {
            case "ASC":
                return "redirect:/catalogs/ASC";
            case "DESC":
                return "redirect:/catalogs/DESC";
            case "notesASC":
                return "redirect:/catalogs/notesASC";
            case "notesDESC":
                return "redirect:/catalogs/notesDESC";
            default:
                return null;
        }
    }

    @GetMapping("/ASC")
    public String sortCatalogsASC(Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        try {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("catalogCookie" + session.getAttribute("userId")))
                    .findAny();
            if (cookie.isPresent()) {
                cookie.get().setValue("ASC");
                response.addCookie(cookie.get());
            }
            List<Catalog> catalogList = catalogService.getCatalogsByUserId((Long) session.getAttribute("userId"));
            catalogList.sort(Comparator.comparing(Catalog::getName));
            _User user = userService.getUserById((Long) session.getAttribute("userId"));
            model.addAttribute("catalogs", catalogList);
            model.addAttribute("user", user);
            logger.info("CatalogsController.sortCatalogsASC(): Sorted catalogs in ascending order for user ID: {}", session.getAttribute("userId"));
            return "catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.sortCatalogsASC(): Error sorting catalogs in ascending order: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/DESC")
    public String sortCatalogsDESC(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("catalogCookie" + session.getAttribute("userId")))
                    .findAny();
            if (cookie.isPresent()) {
                cookie.get().setValue("DESC");
                response.addCookie(cookie.get());
            }
            List<Catalog> catalogList = catalogService.getCatalogsByUserId((Long) session.getAttribute("userId"));
            catalogList.sort(Comparator.comparing(Catalog::getName).reversed());
            _User user = userService.getUserById((Long) session.getAttribute("userId"));
            model.addAttribute("catalogs", catalogList);
            model.addAttribute("user", user);
            logger.info("CatalogsController.sortCatalogsDESC(): Sorted catalogs in descending order for user ID: {}", session.getAttribute("userId"));
            return "catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.sortCatalogsDESC(): Error sorting catalogs in descending order: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/notesASC")
    public String sortCatalogsByNotesASC(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("catalogCookie" + session.getAttribute("userId")))
                    .findAny();
            if (cookie.isPresent()) {
                cookie.get().setValue("notesASC");
                response.addCookie(cookie.get());
            }
            List<Catalog> catalogList = catalogService.getCatalogsByUserId((Long) session.getAttribute("userId"));
            catalogList.sort(Comparator.comparing(Catalog::getNoteCount));
            _User user = userService.getUserById((Long) session.getAttribute("userId"));
            model.addAttribute("catalogs", catalogList);
            model.addAttribute("user", user);
            logger.info("CatalogsController.sortCatalogsByNotesASC(): Sorted catalogs by note count in ascending order for user ID: {}", session.getAttribute("userId"));
            return "catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.sortCatalogsByNotesASC(): Error sorting catalogs by note count in ascending order: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/notesDESC")
    public String sortCatalogsByNotesDESC(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("catalogCookie" + session.getAttribute("userId")))
                    .findAny();
            if (cookie.isPresent()) {
                cookie.get().setValue("notesDESC");
                response.addCookie(cookie.get());
            }
            List<Catalog> catalogList = catalogService.getCatalogsByUserId((Long) session.getAttribute("userId"));
            catalogList.sort(Comparator.comparing(Catalog::getNoteCount).reversed());
            _User user = userService.getUserById((Long) session.getAttribute("userId"));
            model.addAttribute("catalogs", catalogList);
            model.addAttribute("user", user);
            logger.info("CatalogsController.sortCatalogsByNotesDESC(): Sorted catalogs by note count in descending order for user ID: {}", session.getAttribute("userId"));
            return "catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.sortCatalogsByNotesDESC(): Error sorting catalogs by note count in descending order: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/deleteFilters")
    public String deleteFilters(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("catalogCookie" + session.getAttribute("userId")))
                    .findAny();
            if (cookie.isPresent()) {
                cookie.get().setValue("default");
                response.addCookie(cookie.get());
            }
            logger.info("CatalogsController.deleteFilters(): Deleted filters cookie for user ID: {}", session.getAttribute("userId"));
            return "redirect:/catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.deleteFilters(): Error deleting filters cookie: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/createCatalog")
    public String redirectCreate(Model model, HttpSession session) {
        try {
            Catalog catalog = new Catalog();
            _User user = userService.getUserById((Long) session.getAttribute("userId"));
            model.addAttribute("catalog", catalog);
            model.addAttribute("user", user);
            return "createCatalog";
        } catch (Exception e) {
            logger.error("CatalogsController.redirectCreate(): Error redirecting to create catalog page: {}", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/createCatalog")
    public String addCatalog(@Valid @ModelAttribute("catalog") Catalog catalog, BindingResult bindingResult, Model model, HttpSession session) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("errors", bindingResult);
                return "createCatalog";
            }
            catalogService.saveCatalog(catalog, (Long) session.getAttribute("userId"));
            logger.info("CatalogsController.addCatalog(): Created new catalog '{}' for user ID: {}", catalog.getName(), session.getAttribute("userId"));
            return "redirect:/catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.addCatalog(): Error creating catalog: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/deleteCatalog/{catalogId}")
    public String delete(@PathVariable("catalogId") Long catalogId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            Catalog catalog = catalogService.getCatalogById(catalogId);
            if (catalog.getUser().getId().equals(userId) && !(catalog.getName().equals("default")) && !(catalog.getName().equals("shared"))) {
                catalogService.deleteCatalogById(catalogId);
                logger.info("CatalogsController.delete(): Deleted catalog with ID '{}' for user ID: {}", catalogId, userId);
            } else {
                logger.warn("CatalogsController.delete(): User ID {} attempted to delete catalog with ID {} unauthorized.", userId, catalogId);
            }
            return "redirect:/catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.delete(): Error deleting catalog with ID '{}': {}", catalogId, e.getMessage());
            return "error";
        }
    }

    @GetMapping("/editCatalog/{catalogId}")
    public String redirectEdit(Model model, @PathVariable("catalogId") Long catalogId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            Catalog catalog = catalogService.getCatalogById(catalogId);
            if (catalog.getUser().getId().equals(userId) && !(catalog.getName().equals("default")) && !(catalog.getName().equals("shared"))) {
                model.addAttribute("catalog", catalog);
                logger.info("CatalogsController.redirectEdit(): Fetched catalog data for editing: {}", catalogId);
                return "editCatalog";
            } else {
                logger.warn("CatalogsController.redirectEdit(): User ID {} attempted to edit catalog with ID {} unauthorized.", userId, catalogId);
                return "redirect:/catalogs";
            }
        } catch (Exception e) {
            logger.error("CatalogsController.redirectEdit(): Error redirecting to edit catalog page: {}", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/editCatalog/{catalogId}")
    @Transactional
    public String editCatalog(@Valid @ModelAttribute("catalog") Catalog catalog, BindingResult bindingResult, @PathVariable("catalogId") Long catalogId, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("errors", bindingResult);
                catalog.setId(catalogId);
                model.addAttribute("catalog", catalog);
                logger.warn("CatalogsController.editCatalog(): Validation errors encountered when editing catalog with ID: {}", catalogId);
                return "editCatalog";
            }
            catalogService.updateCatalog(catalog, catalogId);
            logger.info("CatalogsController.editCatalog(): Updated catalog with ID '{}'", catalogId);
            return "redirect:/catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.editCatalog(): Error updating catalog with ID '{}': {}", catalogId, e.getMessage());
            return "error";
        }
    }

    @PostMapping("/search")
    public String search(@RequestParam(name = "search") String search, HttpSession session, Model model) {
        try {
            List<Catalog> catalogList = catalogService.getCatalogsByUserId((Long) session.getAttribute("userId"));
            _User user = userService.getUserById((Long) session.getAttribute("userId"));
            List<Catalog> filteredCatalogs = catalogList.stream().filter(c -> c.getName().contains(search)).toList();
            model.addAttribute("catalogs", filteredCatalogs);
            model.addAttribute("user", user);
            logger.info("CatalogsController.search(): Performed search '{}' for user ID: {}", search, session.getAttribute("userId"));
            return "catalogs";
        } catch (Exception e) {
            logger.error("CatalogsController.search(): Error searching catalogs: {}", e.getMessage());
            return "error";
        }
    }
}