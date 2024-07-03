package projet.springmvc.web.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProfesseurController {
    @GetMapping("/ac/professeur")
    String listerProfesseur(Model model,
                           @RequestParam(defaultValue = "0")int page,
                           @RequestParam(defaultValue = "5")int size,
                           @RequestParam(required = false) String grade,
                           @RequestParam(required = false) String portable,
                            @RequestParam(defaultValue = "") Long professeur
                       );

    @GetMapping("/professeur/details/classe/{profID}")
    String listerProfesseurDetailClasse(Model model, @PathVariable Long profID);

    @GetMapping("/professeur/details/module/{profID}")
    String listerProfesseurDetailModule(Model model, @PathVariable Long profID);
}
