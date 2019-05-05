package projekti;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    AccountService accountService;
    
    @Autowired
    ProfileRepository profileRepository;
    
    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        AccountForm accountForm = new AccountForm();
        model.addAttribute("accountForm", accountForm);
        return "register";
    }

    @PostMapping("/register")
    public String addAccount(@ModelAttribute("accountForm") AccountForm accountForm, BindingResult bindingResult) {
        
        AccountFormValidator formValidator = new AccountFormValidator();
        formValidator.validate(accountForm, bindingResult);
                
        if (accountService.isUsernameTaken(accountForm.getUsername())) {
            bindingResult.rejectValue("username", "error.usernameTaken", "Käyttäjätunnus on jo käytössä");
        }
        
        if (profileRepository.existsById(accountForm.getProfile())) {
            bindingResult.rejectValue("profile", "error.profileTaken", "Profiilin osoite on jo käytössä");
        }
        
        if (bindingResult.hasErrors()) {
            return "register";
        }

        accountService.registerNewUserAccount(accountForm);
        return "redirect:/login";
    }
    
    @GetMapping("/search")
    public String getSearch(Model model, @RequestParam String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        
        if (account != null) {
            model.addAttribute("profileId", account.getProfile().getId());
            model.addAttribute("friends", account.getFriends());
        } else {
            model.addAttribute("profileId", null);
        }
        List<Account> results = accountService.findAccountsByName(name);
        model.addAttribute("results", results);
        return "search_accounts";
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/friendrequest/{id}")
    public String sendFriendRequest(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        accountService.sendFriendRequest(account, id);
        return "redirect:/friends";
    }
    
    @Secured("ROLE_POSTER")
    @GetMapping("/friends")
    public String showFriends(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        model.addAttribute("profileId", account.getProfile().getId());
        model.addAttribute("friends", account.getFriends());
        model.addAttribute("received", account.getReceivedFriendRequests());
        model.addAttribute("sent", account.getSentFriendRequests());
        return "friends";
    }
    
    @Secured("ROLE_POSTER")
    @PostMapping("/friendrequest/accept/{id}")
    public String acceptRequest(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        accountService.acceptFriendRequest(account, id);
        return "redirect:/friends";
    }
    
    @Secured("ROLE_POSTER")
    @PostMapping("/friendrequest/decline/{id}")
    public String declineRequest(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        accountService.declineFriendRequest(account, id);
        return "redirect:/friends";
    }
}
