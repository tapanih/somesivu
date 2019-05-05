package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FriendRequestRepository requestRepository;
     
    @Transactional
    public void registerNewUserAccount(AccountForm accountForm) {
         
        Account account = new Account();    
        
        account.setUsername(accountForm.getUsername());
        account.setPassword(passwordEncoder.encode(accountForm.getPassword()));
        account.setName(accountForm.getName());
        account.setProfile(new Profile(accountForm.getProfile(), account, new ArrayList<>()));
        account.setGallery(new Gallery(accountForm.getProfile(), account, new HashSet<>(), new ArrayList<>()));
        
        accountRepository.save(account); 
    }
    
    public boolean isOwnerOfGallery(Account account, String galleryId) {
        return account != null && account.getGallery().getProfileId().equals(galleryId);                
    }
    
    public boolean isOwnerOfProfile(Account account, String galleryId) {
        return account != null && account.getProfile().getId().equals(galleryId);                
    }
    
    public boolean isUsernameTaken(String username) {
        return accountRepository.existsByUsername(username);
    }
    
    public Account findAuthenticatedAccount(String username) {
        return accountRepository.findByUsername(username);
    }
    
    public List<Account> findAccountsByName(String name) {
        return accountRepository.findByNameContainingIgnoreCase(name);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }
    
    public void sendFriendRequest(Account sender, Long recipientId) {
        Optional<Account> result = accountRepository.findById(recipientId);
        if (!result.isPresent()) {
            return;
        }
        Account recipient = result.get();
        
        if (sender.getFriends().contains(recipient)) {
            return;
        }
        
        if (recipient.getReceivedFriendRequests().stream().filter(o -> o.getSender().equals(sender)).findFirst().isPresent()) {
            return;
        }
        FriendRequest friendRequest = new FriendRequest(sender, recipient, LocalDateTime.now());
        recipient.getReceivedFriendRequests().add(friendRequest);
        sender.getSentFriendRequests().add(friendRequest);
        requestRepository.save(friendRequest);
        accountRepository.save(sender);
        accountRepository.save(recipient);
    }

    public void acceptFriendRequest(Account account, Long id) {
        FriendRequest request = requestRepository.getOne(id);
        if (!account.getReceivedFriendRequests().contains(request)) {
            return;
        }
        Account sender = request.getSender();
        sender.getSentFriendRequests().remove(request);
        account.getReceivedFriendRequests().remove(request);
        sender.getFriends().add(account);
        account.getFriends().add(sender);
        accountRepository.save(sender);
        accountRepository.save(account);
        requestRepository.delete(request);        
    }

    public void declineFriendRequest(Account account, Long id) {
        FriendRequest request = requestRepository.getOne(id);
        if (!account.getReceivedFriendRequests().contains(request)) {
            return;
        }
        Account sender = request.getSender();
        sender.getSentFriendRequests().remove(request);
        account.getReceivedFriendRequests().remove(request);
        accountRepository.save(sender);
        accountRepository.save(account);
        requestRepository.delete(request);   
    }
}
