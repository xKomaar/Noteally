package pl.noteally.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.noteally.domain.Catalog;
import pl.noteally.domain._User;
import pl.noteally.repositories.CatalogRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CatalogService {
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    final private CatalogRepository catalogRepository;
    final private UserService userService;

    public Catalog getCatalogById(Long id) {
        return catalogRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("CatalogService.getCatalogById(): Catalog with id: {} not found", id);
                    return new EntityNotFoundException("Catalog not found with id: " + id);
                });
    }

    public List<Catalog> getCatalogsByUserId(Long userId) {
        return catalogRepository.findByUserId(userId);
    }
    @Transactional
    public void saveCatalog(Catalog catalog, Long userId) {
        try {
            _User user = userService.getUserById(userId);
            catalog.setUser(user);
            catalogRepository.save(catalog);
            logger.info("CatalogService.saveCatalog(): Catalog saved successfully, Catalog Data: {}", catalog);
        } catch (EntityNotFoundException e) {
            logger.error("CatalogService.saveCatalog(): Failed to save catalog, Reason: {}", e.getMessage());
            throw new RuntimeException("Failed to save catalog: " + e.getMessage(), e);
        }
    }
    @Transactional
    public void updateCatalog(Catalog catalog, Long catalogId) {
        Catalog existingCatalog = catalogRepository.findById(catalogId)
                .orElseThrow(() -> {
                    logger.error("CatalogService.updateCatalog(): Catalog with id: {} not found", catalogId);
                    return new EntityNotFoundException("Catalog not found with id: " + catalogId);
                });
        existingCatalog.setName(catalog.getName());
        catalogRepository.save(existingCatalog);
        logger.info("CatalogService.updateCatalog(): Catalog with id: {} updated successfully, new Catalog Data: {}", catalogId, existingCatalog);
    }
    @Transactional
    public void deleteCatalogById(Long catalogId) {
        if (!catalogRepository.existsById(catalogId)) {
            logger.error("CatalogService.deleteCatalogById(): Catalog with id: {} not found", catalogId);
            throw new EntityNotFoundException("Catalog not found with id: " + catalogId);
        }
        catalogRepository.deleteById(catalogId);
        logger.info("CatalogService.deleteCatalogById(): Catalog with id: {} deleted successfully", catalogId);
    }
}
