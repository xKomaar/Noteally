package pl.isNamePolishREST.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.isNamePolishREST.services.IsNamePolishService;

@RestController
@AllArgsConstructor
public class IsNamePolishController {

    private final IsNamePolishService isNamePolishService;

    @GetMapping("/")
    public boolean returnFalse() {
        return false;
    }

    @GetMapping("/{name}")
    public boolean contains(@PathVariable String name){
        return isNamePolishService.isNamePolish(name);
    }

}
