package pl.noteally.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.noteally.data.Catalog;
import pl.noteally.data.User;
import pl.noteally.repositories.CatalogRepository;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CatalogService {
    final private CatalogRepository catalogRepository;

    final private UserService userService;

    public Optional<Catalog> getCatalogById(Integer id) {
        return catalogRepository.findById(id);
    }

    public List<Catalog> getCatalogsByUserId(Integer userId) {return catalogRepository.findByUserId(userId);}

    public void saveCatalog(Catalog catalog, Integer userId)
    {
        Optional<User> user = userService.getUserById(userId);
        catalog.setUser(user.get());
        catalogRepository.save(catalog);
    }

    public void updateCatalog(Catalog catalog, Integer catalogId)
    {
        Optional<Catalog> existingCatalog = catalogRepository.findById(catalogId);
        existingCatalog.get().setName(catalog.getName());
        catalogRepository.save(existingCatalog.get());
    }

    public void deleteCatalogById(Integer catalogId)
    {
        catalogRepository.deleteById(catalogId);
    }
}
