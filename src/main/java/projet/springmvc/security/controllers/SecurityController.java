package projet.springmvc.security.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public interface SecurityController {
    @GetMapping({ "/"})
    String login(@AuthenticationPrincipal UserDetails userDetails);

    @GetMapping("/login")
    String showForm();

}
