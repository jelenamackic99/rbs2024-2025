package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.domain.User;
import com.zuehlke.securesoftwaredevelopment.domain.Voucher;
import com.zuehlke.securesoftwaredevelopment.repository.PersonRepository;
import com.zuehlke.securesoftwaredevelopment.repository.RatingRepository;
import com.zuehlke.securesoftwaredevelopment.repository.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VoucherController {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherController.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(VoucherController.class);

    private VoucherRepository voucherRepository;

    public VoucherController(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @PreAuthorize("hasAuthority('NEW_VOUCHER')")
    @GetMapping("/new-voucher")
    public String showAddVoucher(Model model) {
        LOG.info("Pristup stranici za kreiranje vaučera.");
        List<Voucher> voucherList = voucherRepository.getAll();
        model.addAttribute("vouchers", voucherList);
        return "/new-voucher";
    }

    @PreAuthorize("hasAuthority('NEW_VOUCHER')")
    @PostMapping("/new-voucher")
    public String addVoucher(@RequestParam("value") int value,
                             @RequestParam("code") String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            auditLogger.audit(String.format("KREIRAN VAUČER: Korisnik ID: '%s', Kod: '%s', Vrijednost: '%s'",
                    user.getId(), code, value));

            LOG.info("Vaučer kreiran. Korisnik: {}. Kod: {}", user.getUsername(), code);
            voucherRepository.create(user.getId(), code, value);
        }
        return "redirect:/new-voucher";
    }

}
