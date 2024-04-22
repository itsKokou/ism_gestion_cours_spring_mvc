package projet.springmvc.security.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import projet.springmvc.security.controllers.SecurityController;
import projet.springmvc.security.services.SecurityService;

@Controller
@RequiredArgsConstructor
public class SecurityControllerImpl implements SecurityController {

    private  final SecurityService securityService;
    @Override
    public String login(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getAuthorities().stream().anyMatch(role->role.getAuthority().compareTo("ROLE_AC")==0 || role.getAuthority().compareTo("ROLE_RP")==0 || role.getAuthority().compareTo("ROLE_ADMIN")==0 )){
            return "redirect:/ac/cours";
        }
        return "redirect:/login";
    }

    @Override
    public String showForm() {
        return "security/login";
    }

}
