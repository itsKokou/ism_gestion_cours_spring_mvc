package projet.springmvc.security.services.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projet.core.data.entities.AppRole;
import projet.core.data.entities.AppUser;
import projet.core.exceptions.EntityNotFoundException;
import projet.springmvc.security.data.repositories.AppRoleRepository;
import projet.springmvc.security.data.repositories.AppUserRepository;
import projet.springmvc.security.services.SecurityService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService, UserDetailsService  {
    private final AppRoleRepository appRoleRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser getUserByLogin(String login) {
        return appUserRepository.findByLogin(login);
    }

    @Override
    public AppUser saveUser(String login, String password) {
        AppUser user = appUserRepository.findByLogin(login);
        if(user!=null){
            throw new EntityExistsException("Le User existe déjà");
        }
        user = new AppUser(login,passwordEncoder.encode(password));
        return  appUserRepository.save(user);
    }

    @Override
    public AppRole saveRole(String role_name) {
        AppRole role = appRoleRepository.getByRoleName(role_name);
        if(role!=null){
            throw new EntityExistsException("Le Role existe déjà");
        }
        role = new AppRole(role_name);
        return appRoleRepository.save(role);
    }

    @Override
    public void AddRoleToUser(String login, String role_name) {
        AppUser user = appUserRepository.findByLogin(login);
        if(user==null) throw new EntityNotFoundException("User not found");
        AppRole role = appRoleRepository.getByRoleName(role_name);
        if(role==null)throw new EntityNotFoundException("Role not found");
        user.getRoles().add(role);
        appUserRepository.save(user);

    }

    @Override
    public void removeRoleToUser(String login, String role_name) {

    }

    //Strategie d'authentification == connexion
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByLogin(username);
        if(appUser==null) throw  new UsernameNotFoundException("Cet User n'existe pas ");
        List<SimpleGrantedAuthority> authorities = appUser.getRoles().stream().map(appRole -> new SimpleGrantedAuthority(appRole.getRoleName())).toList();
        return new User(
                appUser.getLogin(),
                appUser.getPassword(),
                authorities
                );
    }
}
