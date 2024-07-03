package projet.springmvc.web.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import projet.core.data.dto.ClasseResponseDto;
import projet.core.data.dto.InscriptionEtudiantResponseDto;
import projet.core.data.entities.*;
import projet.core.exceptions.EntityNotFoundException;
import projet.core.services.*;
import projet.springmvc.helpers.PageLoaderService;
import projet.springmvc.web.controllers.SeanceController;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class SeanceControllerImpl implements SeanceController {
    private final SeanceService seanceService;
    private final CoursService coursService;
    private final EtudiantService etudiantService;
    private final ClasseService classeService;
    private final AbsenceService absenceService;
    private final PageLoaderService pageLoaderService;

    public List<String> prepareEvents(List<Seance> seances, int couleur) {

        List<String> events = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Seance seance : seances){
            Professeur prof = seance.getProfesseur() !=null ? seance.getProfesseur() : seance.getCours().getProfesseur();
            String li = seance.getSalle() != null ? "SALLE " + seance.getSalle().getLibelle() : seance.getCodeSeance();
            String lieu = "Lieu : " + li;
            String desc = prof.getNomComplet();
            // Création du format de date
            String date = sdf.format(seance.getDate()) + ' ' + seance.getHeureD().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String newdate = sdf.format(seance.getDate())  + ' ' + seance.getHeureF().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String url = "/ac/seance?id=" + seance.getId();
            JSONObject event = new JSONObject();
            event.put("id",seance.getId());
            event.put("start",date);
            event.put("end",newdate);
            event.put("title",seance.getCours().getModule().getLibelle());
            event.put("description",desc);
            event.put("location",lieu);
            event.put("textColor","#000000");
            event.put("allDay",false);
            event.put("url", url);
            String color;
            if (seance.getIsAbsence()){
                color = "#359EF5";
            }else {
                String date2 = sdf.format(seance.getDate());
                int year = Integer.parseInt(date2.split("-")[0]);
                int month = Integer.parseInt(date2.split("-")[1]);
                int day = Integer.parseInt(date2.split("-")[2]);
                LocalDateTime dateToday  = LocalDateTime.now();
                LocalDateTime dateFinSession = LocalDateTime.of(
                        year,
                        month,
                        day,
                        seance.getHeureF().getHour(),
                        seance.getHeureF().getMinute(),
                        seance.getHeureF().getSecond()
                );
                if (dateToday.isBefore(dateFinSession)){
                    color = "#F53558";
                }else{
                    color ="#35F5C1";
                }
            }
            event.put("color",color);
            if (couleur==0) {
                events.add(event.toJSONString());
            }else if (couleur==1 && color.equals("#F53558")){
                    events.add(event.toJSONString());
            } else if (couleur==2 && color.equals("#35F5C1")){
                events.add(event.toJSONString());
            } else if (couleur==3 && color.equals("#359EF5")){
                events.add(event.toJSONString());
            }
        }
        return events;
    }

    @Override
    public String listerSeance(Model model, int page, int size, Long classe, Long id,int couleur) {
        Optional<Classe> classeOptional = classeService.show(classe);
        List<Seance> allByClasse = seanceService.getAllByClasse(classeOptional.orElse(null));
        List<String> donnees = prepareEvents(allByClasse,couleur);
        model.addAttribute("classes", classeService.getAll(PageRequest.of(0,100)).map(ClasseResponseDto::toDto).getContent());
        model.addAttribute("seances", donnees);
        model.addAttribute("classe", classe);
        model.addAttribute("couleur", couleur);
        model.addAttribute("route", "/ac/seance");

        //---demander faire absence ou voir liste étudiants
        String htmlEtudiant = null;
        if (id!=0){
            htmlEtudiant = pageLoaderService.loadPageContent("http://localhost:8001/seance/etudiant/"+id);
        }
        model.addAttribute("htmlEtudiant", htmlEtudiant);
        model.addAttribute("seanceId", id);

        return "seance/index";
    }

    @Override
    public String listerSeanceEtudiant(Model model, Long seanceId) {
        Seance seance = seanceService.show(seanceId).orElseThrow(()-> new EntityNotFoundException("Seance est non trouvé"));
        Boolean isAbsence = seance.getIsAbsence();
        //checker si session est fini avant marquer absence ou si session pas fini pour annuler
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(seance.getDate());
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);
        LocalDateTime dateToday  = LocalDateTime.now();
        LocalDateTime dateDebutSession = LocalDateTime.of(
                year,
                month,
                day,
                seance.getHeureD().getHour(),
                seance.getHeureD().getMinute(),
                seance.getHeureD().getSecond()
        );
        LocalDateTime dateFinSession = LocalDateTime.of(
                year,
                month,
                day,
                seance.getHeureF().getHour(),
                seance.getHeureF().getMinute(),
                seance.getHeureF().getSecond()
        );

        Duration difference = Duration.between(dateDebutSession,dateToday);

        Boolean finSession = dateFinSession.isBefore(dateToday);
        Boolean debutEmargement = difference.toMinutes()>=30; // emargement débute 30min après le debut seance

        Page<Inscription> listInscrits = seanceService.getLesEtudiantsBySeance(seance, PageRequest.of(0,100));
        Page<Absence> seanceAbsences = absenceService.getByIsArchivedFalseAndSeance(PageRequest.of(0, 200), seance);

        model.addAttribute("isAbsence",isAbsence);
        model.addAttribute("finSession",finSession);
        model.addAttribute("debutEmargement",debutEmargement);
        model.addAttribute("absences", seanceAbsences.map(data->data.getEtudiant().getId()).getContent());
        model.addAttribute("presences",seance.getPresences().stream().map(data->data.getEtudiant().getId()).toList());
        model.addAttribute("etudiants",listInscrits.map(InscriptionEtudiantResponseDto::toDto).getContent());
        return "seance/etudiant";
    }

    //Archiver == invalider
    @Override
    public String archiverSeance(Model model, Long id) {
        Seance seance = seanceService.show(id).orElseThrow(()-> new EntityNotFoundException("Seance est non trouvé"));
        seance.setIsArchived(true);
        Cours cours = seance.getCours();
        int nbreHeure = seance.getHeureF().getHour()-seance.getHeureD().getHour();
        cours.setNbreHeurePlanifie(cours.getNbreHeurePlanifie()-nbreHeure);
        cours.setNbreHeureRestantPlan(cours.getNbreHeureRestantPlan()+nbreHeure);
        seanceService.save(seance);
        coursService.save(cours);
        return "redirect:/ac/seance";
    }

    @Override
    public String saveSeanceAbsences(Model model, Long id, Long[] absentsIds) {

        Seance seance = seanceService.show(id)
                .orElseThrow(()-> new EntityNotFoundException("Seance est non trouvé"));
        seance.setIsAbsence(true);
        // Mis à jour des nbreHeures réalisées du cours
        Cours cours = seance.getCours();
        int nbreHeure = seance.getHeureF().getHour()-seance.getHeureD().getHour();
        cours.setNbreHeureRealise(cours.getNbreHeureRealise()+nbreHeure);
        // Absences
        for (Long absentId : Arrays.stream(absentsIds).toList()) {
            Etudiant etudiant = etudiantService.show(absentId)
                    .orElseThrow(()->new EntityNotFoundException("Etudiant n'existe pas!"));
            Absence absence = new Absence();
            absence.setSeance(seance);
            absence.setEtudiant(etudiant);
            absence.setIsArchived(false);
            absenceService.save(absence);
        }
        coursService.save(cours);
        seanceService.save(seance);
        return "redirect:/ac/seance";
    }
}
