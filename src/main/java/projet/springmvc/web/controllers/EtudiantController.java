package projet.springmvc.web.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface EtudiantController {
    @GetMapping("/ac/etudiant")
    String listerEtudiant(Model model,
                       @RequestParam(defaultValue = "0")int page,
                       @RequestParam(defaultValue = "5")int size,
                       @RequestParam(defaultValue = "0") Long classe,
                       @RequestParam(defaultValue = "") String show,
                       @RequestParam(defaultValue = "") Long etudiant
                       );

    @GetMapping("/ac/etudiant/absence/{etudiantID}")
    String listerEtudiantAbsence(Model model, @PathVariable Long etudiantID);

    @GetMapping("/ac/etudiant/dossier/{etudiantID}")
    String listerEtudiantDossier(Model model, @PathVariable Long etudiantID);
}
