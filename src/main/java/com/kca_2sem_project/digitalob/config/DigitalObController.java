package com.kca_2sem_project.digitalob.config;

import com.kca_2sem_project.digitalob.licencemanagement.License;
import com.kca_2sem_project.digitalob.licencemanagement.LicenseService;
import com.kca_2sem_project.digitalob.usersmanagement.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class DigitalObController {

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private HttpSession session;

    @GetMapping("/")
    public String login(HttpSession session) {
        // If user is already logged in, redirect to appropriate dashboard
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            if ("ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
                return "redirect:/admin_dashboard";
            } else {
                return "redirect:/officer_dashboard";
            }
        }

        // Check license
        License license = licenseService.getCurrentLicense();
        if (!"ACTIVE".equals(license.getStatus()) ||
                LocalDateTime.now().isAfter(license.getExpiryDatetime())) {
            return "redirect:/licence";
        }
        return "login";
    }

    @GetMapping("/admin_dashboard")
    public String adminDashboard(HttpSession session, Model model,
                                 @RequestParam(required = false) String loadFragment) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        if (loadFragment != null) {
            model.addAttribute("initialFragment", loadFragment);
        }

        return "admin_dashboard";
    }

    @GetMapping("/officer_dashboard")
    public String officerDashboard(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"Officer".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }
        return "officer_dashboard";
    }

    @GetMapping("/receipt")
    public String receipt() {
        return "receipt";
    }

    @GetMapping("/licence")
    public String licence() {
        return "licence";
    }

    @GetMapping("/fragments/{page}")
    public String loadPage(@PathVariable String page) {
        return "fragments/" + page;
    }
}