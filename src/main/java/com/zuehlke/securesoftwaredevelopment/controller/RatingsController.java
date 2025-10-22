package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.domain.Comment;
import com.zuehlke.securesoftwaredevelopment.domain.Rating;
import com.zuehlke.securesoftwaredevelopment.domain.User;
import com.zuehlke.securesoftwaredevelopment.repository.CommentRepository;
import com.zuehlke.securesoftwaredevelopment.repository.RatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RatingsController {
    private static final Logger LOG = LoggerFactory.getLogger(RatingsController.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(RatingsController.class);
    private RatingRepository ratingRepository;

    public RatingsController(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @PreAuthorize("hasAuthority('RATE_BOOK')")
    @PostMapping(value = "/ratings", consumes = "application/json")
    public String createOrUpdateRating(@RequestBody Rating rating, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        rating.setUserId(user.getId());
        LOG.info("POKUŠAJ OCJENJIVANJA: Korisnik {} (ID: {}) pokušava ocjeniti knjigu ID: {}.",
                user.getUsername(), user.getId(), rating.getBookId());
        ratingRepository.createOrUpdate(rating);

        auditLogger.audit(String.format("OCJENA KREIRANA/AŽURIRANA: Korisnik ID: '%s', Knjiga ID: '%s', Ocjena: '%s'",
                user.getId(), rating.getBookId(), rating.getRating()));
        LOG.info("OCJENA KREIRANA: Knjiga ID: {}, Korisnik ID: {}. Nova ocjena: {}",
                rating.getBookId(), user.getId(), rating.getRating());
        return "redirect:/books?id=" + rating.getBookId();
    }
}
