package projet.springmvc.security.data.fixtures;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import projet.springmvc.security.services.SecurityService;

//@Component
@RequiredArgsConstructor
//@Order(value = 2)
public class AppRoleFixtures implements CommandLineRunner {
    private final SecurityService securityService;

    @Override
    public void run(String... args) throws Exception {
        securityService.saveRole("ADMIN");
        securityService.saveRole("RP");
        securityService.saveRole("AC");
        securityService.saveRole("PROFESSEUR");
        securityService.saveRole("ETUDIANT");
    }
}
