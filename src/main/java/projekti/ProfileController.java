package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProfileController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ReplyRepository replyRepository;
    
    @GetMapping("/profile")
    public String redirectProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName()); 
        return "redirect:/profile/" + account.getProfile().getId();
    }
    
    @GetMapping("/profile/{profileId}")
    public String getProfile(Model model, @PathVariable String profileId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profiilia ei löytynyt"));
        if (account != null) {
            model.addAttribute("profileId", account.getProfile().getId());
            model.addAttribute("isFriend", account.getFriends().contains(profile.getOwner()));
        } else {
            model.addAttribute("profileId", null);
            model.addAttribute("isFriend", false);
        }

        model.addAttribute("isOwner", accountService.isOwnerOfProfile(account, profileId));
        Pageable pageableMessages = PageRequest.of(0, 25, Sort.by("datetime").descending());
        List<Message> messages = messageRepository.findByProfileId(profileId, pageableMessages);
        model.addAttribute("messages", messages);
        
        Pageable pageableReplies = PageRequest.of(0, 10, Sort.by("datetime").descending());
        List<List<Reply>> replies = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            replies.add(replyRepository.findByParentId(messages.get(i).getId(), pageableReplies));
        }
        model.addAttribute("replies", replies);
        model.addAttribute("profile", profile);
        return "profile";
    }
    
    //TODO: tarkista, että kyseessä on kaveri
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/profile/message/{profileId}")
    public String addMessage(@RequestParam String content, @PathVariable String profileId) {
        if (content != null && !content.isEmpty()) {
            Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profiilia ei löytynyt"));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Account sender = accountService.findAuthenticatedAccount(auth.getName());
            Message message = new Message(profileId, sender, content, LocalDateTime.now(), 0,  new ArrayList<>());
            profile.getMessages().add(message);
            messageRepository.save(message);
            profileRepository.save(profile);
        }

        return "redirect:/profile/" + profileId;
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/profile/reply/{messageId}")
    public String addReply(@RequestParam String content, @PathVariable Long messageId) {        
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viestiä, johon olit vastaamassa, ei löytynyt"));
        Profile profile = profileRepository.findById(message.getProfileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profiilia ei löytynyt"));
        if (content != null && !content.isEmpty()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Account sender = accountService.findAuthenticatedAccount(auth.getName());
            Reply reply = new Reply(message.getId(), profile.getId(), sender, content, LocalDateTime.now(), 0);
            replyRepository.save(reply);
            message.getReplies().add(reply);
            messageRepository.save(message);
            profileRepository.save(profile);
        }
        return "redirect:/profile/" + profile.getId();
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/profile/message/like/{messageId}")
    public String likeMessage(@PathVariable Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viestiä, jota olit tykkäämässä, ei löytynyt"));
        Profile profile = profileRepository.findById(message.getProfileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profiilia ei löytynyt"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        
        if (account.getLikedMessages().contains(messageId)) {
            account.getLikedMessages().remove(messageId);
            message.setNumberOfLikes(message.getNumberOfLikes() - 1);
        } else {
            account.getLikedMessages().add(messageId);
            message.setNumberOfLikes(message.getNumberOfLikes() + 1);
        }
        messageRepository.save(message);
        accountService.save(account);
        return "redirect:/profile/" + profile.getId();
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/profile/reply/like/{replyId}")
    public String likeReply(@PathVariable Long replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viestiä, jota olit tykkäämässä, ei löytynyt"));
        Profile profile = profileRepository.findById(reply.getProfileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profiilia ei löytynyt"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        
        if (account.getLikedReplies().contains(replyId)) {
            account.getLikedReplies().remove(replyId);
            reply.setNumberOfLikes(reply.getNumberOfLikes() - 1);
        } else {
            account.getLikedReplies().add(replyId);
            reply.setNumberOfLikes(reply.getNumberOfLikes() + 1);
        }
        replyRepository.save(reply);
        accountService.save(account);
        return "redirect:/profile/" + profile.getId();
    }
    

}
