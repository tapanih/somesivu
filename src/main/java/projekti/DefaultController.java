package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    
    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }
    
    @GetMapping("/loginsuccess")
    public String redirectToProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        return "redirect:/profile/" + account.getProfile().getId();
    }
}
