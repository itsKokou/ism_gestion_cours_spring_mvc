package projet.springmvc.web.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import projet.core.data.dto.AbsenceResponseDto;
import projet.core.data.entities.Absence;
import projet.core.data.entities.AnneeScolaire;
import projet.core.data.entities.Etudiant;
import projet.core.services.AbsenceService;
import projet.core.services.AnneeScolaireService;
import projet.core.services.EtudiantService;
import projet.springmvc.web.controllers.AbsenceController;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AbsenceControllerImpl implements AbsenceController {

        private final AnneeScolaireService anneeScolaireService;
        private final EtudiantService etudiantService;
        private final AbsenceService absenceService;

    @Override
    public String listerAbsence(Model model, int page, int size, String matricule) {
        Page<Absence> absences;
        Optional<Etudiant> etudiantActuelle = etudiantService.getEtudiantByMatricule(matricule);
        AnneeScolaire anneeScolaire = anneeScolaireService.getAnneeActuelle();
        if (etudiantActuelle.isPresent()){
            absences = absenceService.getAllByAnneeAndEtudiant(PageRequest.of(page,size),anneeScolaire,etudiantActuelle.get());
        }else {
            absences = absenceService.getAllByAnnee(PageRequest.of(page,size),anneeScolaire);
        }

        Page<AbsenceResponseDto> absencesDtos = absences.map(AbsenceResponseDto::toDto);
        model.addAttribute("absences",absencesDtos.getContent());
        model.addAttribute("pages",new int[absencesDtos.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("prev",absencesDtos.hasPrevious());
        model.addAttribute("next",absencesDtos.hasNext());
        model.addAttribute("route","/ac/absence");
        model.addAttribute("totalPage",absencesDtos.getTotalPages());
        model.addAttribute("matricule", matricule);
        model.addAttribute("anneeScolaire", anneeScolaire.getLibelle());

        return "absence/index";
    }
}
