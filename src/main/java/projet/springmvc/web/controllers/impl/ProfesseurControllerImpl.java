package projet.springmvc.web.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import projet.core.data.dto.ClasseResponseDto;
import projet.core.data.dto.ModuleResponseDto;
import projet.core.data.dto.ProfesseurResponseDto;
import projet.core.data.entities.AnneeScolaire;
import projet.core.data.entities.Classe;
import projet.core.data.entities.Module;
import projet.core.data.entities.Professeur;
import projet.core.exceptions.EntityNotFoundException;
import projet.core.services.*;
import projet.springmvc.helpers.PageLoaderService;
import projet.springmvc.web.controllers.ProfesseurController;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfesseurControllerImpl implements ProfesseurController {

        private final AnneeScolaireService anneeScolaireService;
        private final PageLoaderService pageLoaderService;
        private final ProfesseurService professeurService;


    @Override
    public String listerProfesseur(Model model, int page, int size, String grade, String portable, Long professeur) {
        String htmlValueClasse = null;
        String htmlValueModule = null;
        if (professeur!=null){
            htmlValueClasse = pageLoaderService.loadPageContent("http://localhost:8001/ac/professeur/details/classe/"+professeur);
            htmlValueModule = pageLoaderService.loadPageContent("http://localhost:8001/ac/professeur/details/module/"+professeur);
        }
        AnneeScolaire anneeScolaire = anneeScolaireService.getAnneeActuelle();
        Page<Professeur> professeurs = professeurService.getAllByGradeAndPortable(grade,portable ,PageRequest.of(page,size));
        Page<ProfesseurResponseDto> profDtos = professeurs.map(ProfesseurResponseDto::toDto);
        List<String> grades = professeurService.getAllGrades();
        model.addAttribute("professeurs",profDtos.getContent());
        model.addAttribute("pages",new int[profDtos.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("prev",profDtos.hasPrevious());
        model.addAttribute("next",profDtos.hasNext());
        model.addAttribute("route","/ac/professeur");
        model.addAttribute("totalPage",profDtos.getTotalPages());
        model.addAttribute("grades", grades);
        model.addAttribute("grade", grade);
        model.addAttribute("portable", portable);
        model.addAttribute("anneeScolaire", anneeScolaire.getLibelle());
        model.addAttribute("htmlValueClasse", htmlValueClasse);
        model.addAttribute("htmlValueModule", htmlValueModule);

        return "professeur/index";
    }

    @Override
    public String listerProfesseurDetailClasse(Model model, Long profID) {
        Professeur professeur = professeurService.show(profID)
                .orElseThrow(()->new EntityNotFoundException("Professeur n'existe pas"));
        Page<Classe> classes = professeurService.getClasseByProfesseur(professeur,PageRequest.of(0,100));
        Page<ClasseResponseDto> classesDto = classes.map(ClasseResponseDto::toDto);
        model.addAttribute("professeur",professeur.getNomComplet());
        model.addAttribute("classes",classesDto.getContent());
        return "professeur/detailClasse";
    }

    @Override
    public String listerProfesseurDetailModule(Model model, Long profID) {
        Professeur professeur = professeurService.show(profID)
                .orElseThrow(()->new EntityNotFoundException("Professeur n'existe pas"));
        Page<Module> modules = professeurService.getModuleByProfesseur(professeur,PageRequest.of(0,100));
        Page<ModuleResponseDto> modulesDto = modules.map(ModuleResponseDto::toDto);
        model.addAttribute("professeur",professeur.getNomComplet());
        model.addAttribute("modules",modulesDto.getContent());
        return "professeur/detailModule";
    }
}
