package com.kca_2sem_project.digitalob.configs;
import com.kca_2sem_project.digitalob.officers.Officer;
import com.kca_2sem_project.digitalob.officers.OfficerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class DigitalObController {

    @Autowired
    private OfficerService officerService;

    // Landing page
    @GetMapping("/")
    public String landingPage() {
        return "landing";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Process login
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password,
                               HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Optional<Officer> officerOpt = officerService.login(username, password);

        if (officerOpt.isPresent()) {
            Officer officer = officerOpt.get();

            // Create session for logged-in user
            HttpSession session = request.getSession(true);
            session.setAttribute("officerId", officer.getId());
            session.setAttribute("officerName", officer.getName());
            session.setAttribute("officerRole", officer.getRole());

            // Redirect based on role
            if ("Admin".equals(officer.getRole())) {
                return "redirect:/adminDashboard";
            } else {
                return "redirect:/userDashboard";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    // Admin dashboard
    @GetMapping("/adminDashboard")
    public String adminDashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && "Admin".equals(session.getAttribute("officerRole"))) {
            model.addAttribute("officerName", session.getAttribute("officerName"));
            return "adminDashboard";
        } else {
            return "redirect:/login";
        }
    }

    // User dashboard
    @GetMapping("/userDashboard")
    public String userDashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            model.addAttribute("officerName", session.getAttribute("officerName"));
            return "userDashboard";
        } else {
            return "redirect:/login";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
    @GetMapping("/fragments/{page}")
    public String loadPage(@PathVariable String page) {
        return "fragments/" + page;
    }
}