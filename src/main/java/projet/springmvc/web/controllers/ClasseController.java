package projet.springmvc.web.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface ClasseController {
    @GetMapping("/ac/classe")
    String listerClasse(Model model,
                       @RequestParam(defaultValue = "0")int page,
                       @RequestParam(defaultValue = "5")int size,
                       @RequestParam(defaultValue = "0") Long niveau,
                       @RequestParam(defaultValue = "0") Long filiere
                       );
}
