package pl.noteally;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionSystemException;
import pl.noteally.domain.Catalog;
import pl.noteally.domain._User;
import pl.noteally.repositories.CatalogRepository;
import pl.noteally.repositories.UserRepository;
import pl.noteally.services.CatalogService;
import pl.noteally.services.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class CatalogServiceTest {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private _User testUser;

    @BeforeEach
    public void setUp() {
        catalogRepository.deleteAll();
        userRepository.deleteAll();
        testUser = new _User();
        testUser.setEmail("user@example.com");
        testUser.setPassword("password");
        testUser.setName("Jakub");
        testUser.setSurname("Komarewski");
        testUser.setAge(22);
        userService.signUpUser(testUser);
    }

    @Test
    public void testGetCatalogById() {
        Catalog catalog = createCatalog("test catalog");

        Catalog foundCatalog = catalogService.getCatalogById(catalog.getId());
        assertNotNull(foundCatalog);
        assertEquals(catalog.getId(), foundCatalog.getId());
        assertEquals(catalog.getName(), foundCatalog.getName());
    }

    @Test
    public void testGetCatalogByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            catalogService.getCatalogById(999L);
        });
    }

    @Test
    public void testGetCatalogsByUserId() {
        createCatalog("catalog one");
        createCatalog("catalog two");

        List<Catalog> catalogs = catalogService.getCatalogsByUserId(testUser.getId());
        assertEquals(4, catalogs.size());
    }

    @Test
    public void testSaveCatalog() {
        Catalog catalog = new Catalog();
        catalog.setName("new catalog");

        catalogService.saveCatalog(catalog, testUser.getId());

        List<Catalog> catalogs = catalogRepository.findByUserId(testUser.getId());
        //3 because of 2 default catalogs
        assertEquals(3, catalogs.size());
        assertEquals("new catalog", catalogs.get(2).getName());
    }

    @Test
    public void testSaveCatalogValidation() {
        Catalog catalog = new Catalog();

        assertThrows(TransactionSystemException.class, () -> {
            catalogService.saveCatalog(catalog, testUser.getId());
        });
    }

    @Test
    public void testUpdateCatalog() {
        Catalog catalog = createCatalog("old catalog");

        Catalog updatedCatalog = new Catalog();
        updatedCatalog.setName("updated catalog");

        catalogService.updateCatalog(updatedCatalog, catalog.getId());

        Catalog foundCatalog = catalogService.getCatalogById(catalog.getId());
        assertEquals("updated catalog", foundCatalog.getName());
    }

    @Test
    public void testDeleteCatalogById() {
        Catalog catalog = createCatalog("to delete catalog");

        catalogService.deleteCatalogById(catalog.getId());

        assertThrows(EntityNotFoundException.class, () -> {
            catalogService.getCatalogById(catalog.getId());
        });
    }

    @Test
    public void testDeleteCatalogByIdNotFound() {
        Catalog catalog = createCatalog("to delete catalog");

        assertThrows(EntityNotFoundException.class, () -> {
            catalogService.deleteCatalogById(catalog.getId() + 1);
        });
    }

    @Test
    public void testSaveCatalogUserNotFound() {
        Catalog catalog = new Catalog();
        catalog.setName("new catalog");

        assertThrows(RuntimeException.class, () -> {
            catalogService.saveCatalog(catalog, 999L);
        });
    }

    private Catalog createCatalog(String name) {
        Catalog catalog = new Catalog();
        catalog.setName(name);
        catalog.setUser(testUser);
        return catalogRepository.save(catalog);
    }
}
