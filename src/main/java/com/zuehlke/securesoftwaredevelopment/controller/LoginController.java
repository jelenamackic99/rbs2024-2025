package com.zuehlke.securesoftwaredevelopment.controller;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.domain.HashedUser;
import com.zuehlke.securesoftwaredevelopment.repository.HashedUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(LoginController.class); // DODAJEMO AUDITLOGGER

    private final HashedUserRepository repository;

    LoginController(HashedUserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register-totp")
    public String showRegisterTotp(Model model, Authentication authentication) {
        final HashedUser user = (HashedUser) authentication.getPrincipal();
        LOG.info("KORISNIK PRISTUPIO: Korisnik {} pristupio stranici za TOTP registraciju.", user.getUsername());
        GoogleAuthenticator gAuth = new GoogleAuthenticator();

        final GoogleAuthenticatorKey key = gAuth.createCredentials(); // TOTP key
        model.addAttribute("totpKey", key.getKey());

        String totpUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("Book Shop", user.getUsername(), key);
        model.addAttribute("totpUrl", totpUrl);

        return "register-totp";
    }

    @PostMapping("/register-totp")
    public String registerTotp(@RequestParam() String totpKey, Model model, Authentication authentication) {
        final HashedUser user = (HashedUser) authentication.getPrincipal();

        auditLogger.audit(String.format("TOTP REGISTRACIJA: Korisnik username: '%s' uspješno sačuvao TOTP ključ.", user.getUsername()));
        LOG.info("TOTP KLJUČ SAČUVAN: Korisnik {} uspješno sačuvao ključ.", user.getUsername());

        repository.saveTotpKey(user.getUsername(), totpKey);
        model.addAttribute("registered", true);
        return "register-totp";
    }
}
