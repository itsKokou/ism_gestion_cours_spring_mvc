package projet.springmvc.security.services;


import projet.core.data.entities.AppRole;
import projet.core.data.entities.AppUser;

public interface SecurityService {
    AppUser getUserByLogin(String login);
    AppUser saveUser(String login, String password);
    AppRole saveRole(String role_name);
    void AddRoleToUser(String login, String role_name);
    void removeRoleToUser(String login, String role_name);
}
