package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.domain.Person;
import com.zuehlke.securesoftwaredevelopment.domain.Role;
import com.zuehlke.securesoftwaredevelopment.domain.User;
import com.zuehlke.securesoftwaredevelopment.repository.PersonRepository;
import com.zuehlke.securesoftwaredevelopment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.zuehlke.securesoftwaredevelopment.repository.RoleRepository;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@Controller

public class PersonsController {

    private static final Logger LOG = LoggerFactory.getLogger(PersonsController.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(PersonRepository.class);

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public PersonsController(PersonRepository personRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PreAuthorize("hasAuthority('VIEW_PERSON')")
    @GetMapping("/persons/{id}")
    public String person(@PathVariable int id, Model model, Authentication principal, HttpSession session) throws AccessDeniedException {
        String csrfToken = session.getAttribute("CSRF_TOKEN").toString();
        model.addAttribute("CSRF_TOKEN", csrfToken);
        model.addAttribute("person", personRepository.get("" + id));
        model.addAttribute("username", userRepository.findUsername(id));
        return "person";
    }

    @GetMapping("/myprofile")
    public String self(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("person", personRepository.get("" + user.getId()));
        model.addAttribute("username", userRepository.findUsername(user.getId()));
        return "person";
    }

    @DeleteMapping("/persons/{id}")
    public ResponseEntity<Void> person(@PathVariable int id, Authentication principal) {

        User user = (User) principal.getPrincipal();

        List<Role> roles = roleRepository.findByUserId(user.getId());
        Role role = roles.get(0);

        boolean hasUpdatePersonPermission = principal.getAuthorities().contains(new SimpleGrantedAuthority("UPDATE_PERSON"));
        boolean isAdmin = "ADMIN".equals(role.getName());
        boolean isCurrentUser = user.getId() == id;

        LOG.info("POKUŠAJ BRISANJA: Korisnik {} pokušava obrisati ID: {}", user.getUsername(), id);

        if (!(hasUpdatePersonPermission && (isAdmin || isCurrentUser))){
            LOG.warn("PRISTUP ODBIJEN: Korisnik {} nema permisije za brisanje ID: {}", user.getUsername(), id);
            throw new AccessDeniedException("You are not allowed to update this person");
        }


        personRepository.delete(id);
        userRepository.delete(id);

        LOG.info("KORISNIK OBRISAN: Korisnik {} uspješno obrisao ID: {}", user.getUsername(), id);
        auditLogger.audit(String.format("USPJEŠNO BRISANJE: Korisnik ID: '%s' obrisao profil ID: '%s'",
                user.getId(), id));
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/update-person")
    public String updatePerson(Person person, String username,
                               Authentication principal) throws AccessDeniedException {

        User user = (User) principal.getPrincipal();

        List<Role> roles = roleRepository.findByUserId(user.getId());
        Role role = roles.get(0);

        boolean hasUpdatePersonPermission = principal.getAuthorities().contains(new SimpleGrantedAuthority("UPDATE_PERSON"));
        boolean isAdmin = "ADMIN".equals(role.getName());
        boolean isCurrentUser = user.getId() == Integer.parseInt(person.getId());

        LOG.info("POKUŠAJ AŽURIRANJA: Korisnik {} (ID: {}) pokušava promijeniti ID: {}",
                user.getUsername(), user.getId(), person.getId());

        if (!(hasUpdatePersonPermission && (isAdmin || isCurrentUser))){
            LOG.warn("PRISTUP ODBIJEN: Korisnik {} nema permisije za ažuriranje ID: {}", user.getUsername(), person.getId());

            throw new AccessDeniedException("You are not allowed to update this person");
        }
        personRepository.update(person);
        userRepository.updateUsername(Integer.parseInt(person.getId()), username);

        LOG.info("PROFIL AŽURIRAN: Korisnik {} uspješno promijenio podatke za ID: {}", user.getUsername(), person.getId());
        auditLogger.audit(String.format("USPJEŠNO AŽURIRANJE: Korisnik ID: '%s' ažurirao profil ID: '%s', Novo Username: '%s'",
                user.getId(), person.getId(), username));
        return "redirect:/persons/" + person.getId();
    }

    @PreAuthorize("hasAuthority('VIEW_PERSONS_LIST')")
    @GetMapping("/persons")
    public String persons(Model model) {
        model.addAttribute("persons", personRepository.getAll());
        return "persons";
    }

    @GetMapping(value = "/persons/search", produces = "application/json")
    @ResponseBody
    public List<Person> searchPersons(@RequestParam String searchTerm) throws SQLException {
        return personRepository.search(searchTerm);
    }
}
