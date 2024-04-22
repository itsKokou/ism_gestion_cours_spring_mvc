package projet.springmvc.security.data.fixtures;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import projet.springmvc.security.services.SecurityService;

//@Component
@RequiredArgsConstructor
//@Order(value = 1)
public class AppUserFixtures implements CommandLineRunner {
    private final SecurityService securityService;

    @Override
    public void run(String... args) throws Exception {
        securityService.saveUser("admin","passer");
        securityService.AddRoleToUser("admin","ADMIN");
    }
}
