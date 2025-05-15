package net.flectone.osu.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.flectone.osu.model.News;
import net.flectone.osu.model.User;
import net.flectone.osu.repository.UserRepository;
import net.flectone.osu.service.NewsService;
import net.flectone.osu.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;

@Controller
@AllArgsConstructor
public class WebController {

    private final UserService userService;
    private final NewsService newsService;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("newsList", newsService.getAllNews());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "register";
        }

        try {
            userService.createUser(user);
            redirectAttributes.addAttribute("registered", true);
            return "redirect:/login";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Почта уже существует");
            return "register";
        }
    }

    @GetMapping("/add-news")
    public String addNewsForm() {
        return "add-news";
    }

    @PostMapping("/add-news")
    public String addNews(News news) {
        newsService.createNews(news);
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        userRepository.save(user);

        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        userRepository.delete(user);
        SecurityContextHolder.clearContext();

        return "redirect:/login";
    }

    @GetMapping("/edit-news/{id}")
    public String editNewsForm(@PathVariable Long id, Model model) throws AccessDeniedException {
        News news = newsService.getNewsById(id);
        model.addAttribute("news", news);
        return "edit-news";
    }

    @PostMapping("/edit-news/{id}")
    public String updateNews(@PathVariable Long id, @ModelAttribute News news) throws AccessDeniedException {
        newsService.updateNews(id, news);
        return "redirect:/";
    }

    @PostMapping("/delete-news/{id}")
    public String deleteNews(@PathVariable Long id) throws AccessDeniedException {
        newsService.deleteNews(id);
        return "redirect:/";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.changePassword(oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Пароль успешно изменён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}