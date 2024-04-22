package projet.springmvc.web.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projet.core.data.enums.EtatCours;

public interface CoursController {
    @GetMapping("/ac/cours")
    String listerCours(Model model,
                       @RequestParam(defaultValue = "0")int page,
                       @RequestParam(defaultValue = "5")int size,
                       @RequestParam(required = false) EtatCours etat
                       );
}
