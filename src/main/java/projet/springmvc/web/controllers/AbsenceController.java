package projet.springmvc.web.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface AbsenceController {
    @GetMapping("/ac/absence")
    String listerAbsence(Model model,
                       @RequestParam(defaultValue = "0")int page,
                       @RequestParam(defaultValue = "5")int size,
                       @RequestParam(required = false, defaultValue = "") String matricule
                       );
}
