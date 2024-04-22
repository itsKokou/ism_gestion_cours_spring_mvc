package projet.springmvc.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //Redirection des requetes vers ici pour gestion
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService service;
    private final PasswordEncoder passwordEncoder;

    //Authentification
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(service);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    //Autorisation
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        //ON peut dire si on utilise notre loginPage ou celle du sys
        http.csrf(csrfToken->csrfToken.disable());
        return http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(form->{form
                        .loginPage("/login")
                        .permitAll();
                }).authorizeHttpRequests(
                        auth-> auth
                                .requestMatchers("/ac/**").hasAnyAuthority("ROLE_AC","ROLE_RP","ROLE_ADMIN")
                                .anyRequest().permitAll()
                ).logout(LogoutConfigurer::permitAll)
                .build();
        //Form par defaut
//        return http.csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
//                .authorizeHttpRequests(auth-> auth
//                        .requestMatchers("/api/**").permitAll()//pas de security ici
//                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
//                        .requestMatchers("/client/**").hasAnyAuthority("CLIENT","ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .build();
    }
}
