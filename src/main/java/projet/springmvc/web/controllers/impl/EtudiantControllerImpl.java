package projet.springmvc.web.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import projet.core.data.dto.AbsenceResponseDto;
import projet.core.data.dto.ClasseResponseDto;
import projet.core.data.dto.InscriptionEtudiantResponseDto;
import projet.core.data.entities.*;
import projet.core.exceptions.EntityNotFoundException;
import projet.core.services.*;
import projet.springmvc.helpers.PageLoaderService;
import projet.springmvc.web.controllers.EtudiantController;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EtudiantControllerImpl implements EtudiantController {

        private final AnneeScolaireService anneeScolaireService;
        private final EtudiantService etudiantService;
        private final InscriptionService inscriptionService;
        private final ClasseService classeService;
        private final AbsenceService absenceService;
        private final PageLoaderService pageLoaderService;

    @Override
    public String listerEtudiant(Model model, int page, int size, Long classe, String show,Long etudiant) {
        String htmlAbsence = null;
        String htmlDossier = null;
        if (show.equals("absence")){
            htmlAbsence = pageLoaderService.loadPageContent("http://localhost:8001/ac/etudiant/absence/"+etudiant);
        }
        if (show.equals("dossier")){
            htmlDossier = pageLoaderService.loadPageContent("http://localhost:8001/ac/etudiant/dossier/"+etudiant);
        }
        Optional<Classe> classeActuelle = classeService.show(classe);
        AnneeScolaire anneeScolaire = anneeScolaireService.getAnneeActuelle();
        Classe cl = null;
        if (classeActuelle.isPresent()){
            cl = classeActuelle.get();
        }
        Page<Inscription> inscriptions = inscriptionService.getAllByAnneActiveAndClasse(cl, PageRequest.of(page,size));

        Page<InscriptionEtudiantResponseDto> etudiantsDtos = inscriptions.map(InscriptionEtudiantResponseDto::toDto);
        model.addAttribute("etudiants",etudiantsDtos.getContent());
        model.addAttribute("pages",new int[etudiantsDtos.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("prev",etudiantsDtos.hasPrevious());
        model.addAttribute("next",etudiantsDtos.hasNext());
        model.addAttribute("route","/ac/etudiant");
        model.addAttribute("totalPage",etudiantsDtos.getTotalPages());
        model.addAttribute("classe", classe);
        model.addAttribute("classes", classeService.getAll(PageRequest.of(0,100)).map(ClasseResponseDto::toDto));
        model.addAttribute("anneeScolaire", anneeScolaire.getLibelle());
        model.addAttribute("htmlAbsence", htmlAbsence);
        model.addAttribute("htmlDossier", htmlDossier);

        return "etudiant/index";
    }

    @Override
    public String listerEtudiantAbsence(Model model, Long etudiantID) {
        Etudiant etudiant = etudiantService.show(etudiantID).orElseThrow(()->new EntityNotFoundException("Etudiant n'existe pas"));
        AnneeScolaire anneeScolaire = anneeScolaireService.getAnneeActuelle();
        Page<Absence> absences = absenceService.getAllByAnneeAndEtudiant(PageRequest.of(0,100),anneeScolaire,etudiant);
        Page<AbsenceResponseDto> absencesDtos = absences.map(AbsenceResponseDto::toDto);
        model.addAttribute("absences",absencesDtos.getContent());
        model.addAttribute("etudiant",etudiant.getNomComplet());
        model.addAttribute("anneeScolaire", anneeScolaire.getLibelle());

         return "etudiant/absence";
    }

    @Override
    public String listerEtudiantDossier(Model model, Long etudiantID) {
        Etudiant etudiant = etudiantService.show(etudiantID)
                .orElseThrow(()->new EntityNotFoundException("Etudiant n'existe pas"));
        AnneeScolaire anneeScolaire = anneeScolaireService.getAnneeActuelle();
        Inscription inscription = inscriptionService.getByAndAnneeScolaireAndEtudiant(anneeScolaire,etudiant)
                .orElseThrow(()->new EntityNotFoundException("Inscription n'existe pas"));
        List<Inscription> inscriptions = etudiant.getInscriptions();
        model.addAttribute("inscriptions",inscriptions);
        model.addAttribute("etudiant",etudiant);
        model.addAttribute("inscription", inscription);
        return "etudiant/dossier";
    }
}
