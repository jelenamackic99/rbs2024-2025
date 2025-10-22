package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.domain.Comment;
import com.zuehlke.securesoftwaredevelopment.domain.User;
import com.zuehlke.securesoftwaredevelopment.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpSession;

@Controller
public class CommentController {
    private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(CommentController.class); // DODAJ OVO
    private CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @PreAuthorize("hasAuthority('ADD_COMMENT')")
    @PostMapping(value = "/comments", consumes = "application/json")
    public ResponseEntity<Void> createComment(@RequestBody Comment comment, Authentication authentication,
                                              HttpSession session,
                                              @RequestHeader("X-CSRF-TOKEN") String headerToken){
        String sessionToken = (String) session.getAttribute("MANUAL_CSRF_TOKEN");

        if (sessionToken == null || headerToken == null || !sessionToken.equals(headerToken)) {
            LOG.warn("CSRF NAPAD ODBIJEN: Neispravan token za korisnika {}", authentication.getName());
            throw new AccessDeniedException("Neispravan CSRF token.");
        }

        // Ako je provjera prošla, ukloni token da se ne može ponovo koristiti
        session.removeAttribute("MANUAL_CSRF_TOKEN");


        User user = (User) authentication.getPrincipal();
        comment.setUserId(user.getId());

        LOG.info("POKUŠAJ KREIRANJA KOMENTARA: Korisnik {} (ID: {}) za knjigu ID: {}",
                user.getUsername(), user.getId(), comment.getBookId());
        commentRepository.create(comment);

        auditLogger.audit(String.format("USPJEŠNO KREIRANJE KOMENTARA: Korisnik ID: '%s', Knjiga ID: '%s'",
                user.getId(), comment.getBookId()));

        LOG.info("KOMENTAR KREIRAN: Knjiga ID: {}, Korisnik ID: {}.", comment.getBookId(), user.getId());
        return ResponseEntity.noContent().build();
    }
}
