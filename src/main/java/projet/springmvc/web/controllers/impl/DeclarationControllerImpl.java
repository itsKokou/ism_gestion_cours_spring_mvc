package projet.springmvc.web.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import projet.core.data.dto.DeclarationResponseDto;
import projet.core.data.entities.*;
import projet.core.data.enums.EtatDeclaration;
import projet.core.exceptions.EntityNotFoundException;
import projet.core.services.*;
import projet.springmvc.web.controllers.DeclarationController;

@Controller
@RequiredArgsConstructor
public class DeclarationControllerImpl implements DeclarationController {

        private final AnneeScolaireService anneeScolaireService;
        private final EtudiantService etudiantService;
        private final AbsenceService absenceService;
        private final DeclarationService declarationService;
        private final SeanceService seanceService;
        private final PresenceService presenceService;

    @Override
    public String listerDeclaration(Model model, int page, int size, EtatDeclaration etat) {
        Page<Declaration> declarations = declarationService.getDeclarationsByEtatAndUserRole(etat,"ROLE_ETUDIANT",PageRequest.of(page,size));
        AnneeScolaire anneeScolaire = anneeScolaireService.getAnneeActuelle();
        Page<DeclarationResponseDto> declarationDtos = declarations.map(DeclarationResponseDto::toDto);
        model.addAttribute("declarations",declarationDtos.getContent() );
        model.addAttribute("pages",new int[declarationDtos.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("prev",declarationDtos.hasPrevious());
        model.addAttribute("next",declarationDtos.hasNext());
        model.addAttribute("route","/ac/declaration");
        model.addAttribute("totalPage",declarationDtos.getTotalPages());
        model.addAttribute("anneeScolaire", anneeScolaire.getLibelle());
        model.addAttribute("etats", EtatDeclaration.values());
        model.addAttribute("etatActuel", etat);
        model.addAttribute("etatActuelString", etat.name());
        return "declaration/index";
    }

    @Override
    public String traiterDeclaration(Long id, String user, Long userId, Long seanceId, String action) {
        Declaration declaration = declarationService.show(id)
                .orElseThrow(()->new EntityNotFoundException("Declaration non trouvée !"));
        if (action.equals("refuser")){
            declaration.setEtat(EtatDeclaration.Refuse);
        }else{
            declaration.setEtat(EtatDeclaration.Accepte);
            Seance seance = seanceService.show(seanceId)
                    .orElseThrow(()->new EntityNotFoundException("Seance non trouvée !"));

            if (user.equals("etudiant")){
                Etudiant etudiant = etudiantService.show(userId).orElseThrow(()->new EntityNotFoundException("Etudiant non trouvé !"));
                //Si declaration est venu avant debut seance alors on enregistre la presence
                //Si declaration après fin(absences enregistrées) alors archiver absence
                if (!seance.getIsAbsence()){
                    //Avant enregistrement
                    Presence presence = new Presence();
                    presence.setEtudiant(etudiant);
                    presence.setSeance(seance);
                    presence.setIsArchived(false);
                    presenceService.save(presence);
                }else {
                    //Après enregistrement absence et etudiant enregistrer comme absent sinon absence = null
                    Absence absence = absenceService.getAbsenceByEtudiantAndSeance(etudiant,seance).orElse(null);
                    if(absence != null){
                        absence.setIsArchived(true);
                        absenceService.save(absence);
                    }
                }

            }
//            else if (user.equals("professeur")) {
//                //Annuler la seance
//                Cours cours = seance.getCours();
//                int nbreHeure = seance.getHeureF().getHour()-seance.getHeureD().getHour();
//                cours.setNbreHeurePlanifie(cours.getNbreHeurePlanifie()-nbreHeure);
//                cours.setNbreHeureRestantPlan(cours.getNbreHeureRestantPlan()+nbreHeure);
//                seance.setIsArchived(true);
//                seanceService.save(seance);
//                coursService.save(cours);
//            }
        }
        declarationService.save(declaration);
    return "redirect:/ac/declaration";
    }
}
