package projet.springmvc.web.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import projet.core.data.dto.CoursResponseDto;
import projet.core.data.entities.AnneeScolaire;
import projet.core.data.entities.Cours;
import projet.core.data.enums.EtatCours;
import projet.core.services.AnneeScolaireService;
import projet.core.services.CoursService;
import projet.springmvc.web.controllers.CoursController;

@Controller
@RequiredArgsConstructor
public class CoursControllerImpl implements CoursController {

    private final CoursService coursService;
    private final AnneeScolaireService anneeScolaireService;

    @Override
    public String listerCours(Model model,
                               @RequestParam(defaultValue = "0")int page,
                               @RequestParam(defaultValue = "5")int size,
                               @RequestParam(required = false) EtatCours etat){
        Page<Cours> courss;
        AnneeScolaire anneeScolaire = anneeScolaireService.getAnneeActuelle();
        if (etat!=null){
            courss = coursService.getAllByAnneeAndEtat(PageRequest.of(page,size),anneeScolaire,etat);
        }else {
            courss = coursService.getAllByAnnee(PageRequest.of(page,size),anneeScolaire);
        }

        Page<CoursResponseDto> coursDtos = courss.map(CoursResponseDto::toDto);
        model.addAttribute("courss",coursDtos.getContent());
        model.addAttribute("pages",new int[coursDtos.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("prev",coursDtos.hasPrevious());
        model.addAttribute("next",coursDtos.hasNext());
        model.addAttribute("route","/ac/cours");
        model.addAttribute("totalPage",coursDtos.getTotalPages());
        model.addAttribute("etats", EtatCours.values());
        model.addAttribute("etatActuel", etat);
        model.addAttribute("anneeScolaire", anneeScolaire.getLibelle());

        return "cours/index";
    }
}
