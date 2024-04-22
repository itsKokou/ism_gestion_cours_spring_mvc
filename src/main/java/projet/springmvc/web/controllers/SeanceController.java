package projet.springmvc.web.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface SeanceController {
    @GetMapping("/ac/seance")
    String listerSeance(Model model,
                          @RequestParam(defaultValue = "0")int page,
                          @RequestParam(defaultValue = "5")int size,
                          @RequestParam(defaultValue = "0") Long classe,
                          @RequestParam(defaultValue = "0") Long id,
                          @RequestParam(defaultValue = "0") int couleur
    );

    @GetMapping("/ac/seance/etudiant/{seanceId}")
    String listerSeanceEtudiant(Model model, @PathVariable Long seanceId);

//    @GetMapping("/ac/seance/archiver/{id}")
//    String archiverSeance(Model model, @PathVariable Long id);

    @GetMapping("/ac/seance/{id}/absences/save/{absentsIds}")
    String saveSeanceAbsences(Model model,
                              @PathVariable Long id,
                              @PathVariable Long[] absentsIds);

}
