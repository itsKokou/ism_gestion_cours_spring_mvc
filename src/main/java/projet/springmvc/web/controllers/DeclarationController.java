package projet.springmvc.web.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import projet.core.data.enums.EtatDeclaration;

public interface DeclarationController {
    @GetMapping("/ac/declaration")
    String listerDeclaration(Model model,
                             @RequestParam(defaultValue = "0")int page,
                             @RequestParam(defaultValue = "5")int size,
                             @RequestParam(defaultValue = "Enattente") EtatDeclaration etat
                             );

    @GetMapping("/ac/declaration/{id}/{user}/{userId}/seance/{seanceId}/{action}")
    String traiterDeclaration(@PathVariable Long id,
                              @PathVariable String user,
                              @PathVariable Long userId,
                              @PathVariable Long seanceId,
                              @PathVariable String action);
}
