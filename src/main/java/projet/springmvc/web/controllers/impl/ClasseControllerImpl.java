package projet.springmvc.web.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import projet.core.data.dto.ClasseResponseDto;
import projet.core.data.dto.FiliereResponseDto;
import projet.core.data.dto.NiveauResponseDto;
import projet.core.data.entities.AnneeScolaire;
import projet.core.data.entities.Classe;
import projet.core.data.entities.Filiere;
import projet.core.data.entities.Niveau;
import projet.core.services.AnneeScolaireService;
import projet.core.services.ClasseService;
import projet.core.services.FiliereService;
import projet.core.services.NiveauService;
import projet.springmvc.web.controllers.ClasseController;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ClasseControllerImpl implements ClasseController {

        private final AnneeScolaireService anneeScolaireService;
        private final ClasseService classeService;
        private final NiveauService niveauService;
        private final FiliereService filiereService;

    @Override
    public String listerClasse(Model model, int page, int size, Long niveau, Long filiere) {
        Page<Classe> classes;
        Optional<Niveau> niveauActuelle = niveauService.show(niveau);
        Optional<Filiere> filiereActuelle = filiereService.show(filiere);
        AnneeScolaire anneeScolaire = anneeScolaireService.getAnneeActuelle();
        Niveau niv = null;
        Filiere fil = null;
        if(niveauActuelle.isPresent()){
            niv = niveauActuelle.get();
        }
        if(filiereActuelle.isPresent()){
            fil = filiereActuelle.get();
        }
       classes = classeService.getAllByNiveauAndFiliereAndPlanned(PageRequest.of(page,size),niv,fil,true);
        Page<NiveauResponseDto> listNiveau = niveauService.getAll(PageRequest.of(0,100)).map(NiveauResponseDto::toDto);
        Page<FiliereResponseDto> listFiliere = filiereService.getAll(PageRequest.of(0,100)).map(FiliereResponseDto::toDto);

        Page<ClasseResponseDto> classesDtos = classes.map(ClasseResponseDto::toDto);
        model.addAttribute("classes",classesDtos.getContent());
        model.addAttribute("pages",new int[classesDtos.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("prev",classesDtos.hasPrevious());
        model.addAttribute("next",classesDtos.hasNext());
        model.addAttribute("route","/ac/classe");
        model.addAttribute("totalPage",classesDtos.getTotalPages());
        model.addAttribute("niveau", niveau);
        model.addAttribute("niveaux", listNiveau.getContent());
        model.addAttribute("filiere", filiere);
        model.addAttribute("filieres", listFiliere.getContent());
        model.addAttribute("anneeScolaire", anneeScolaire.getLibelle());

        return "classe/index";
    }
}
