package projekti;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Account extends AbstractPersistable<Long> {
        
    private String username;
    private String password;
    private String name;
    
    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;
    
    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.ALL)
    private Gallery gallery;
    
    @EqualsAndHashCode.Exclude
    private Long avatarId = null;
    
    @EqualsAndHashCode.Exclude
    @ElementCollection
    private Set<Long> likedMessages = new HashSet<>();
    @EqualsAndHashCode.Exclude
    @ElementCollection
    private Set<Long> likedReplies = new HashSet<>();
    
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    @JoinTable(name = "friends",
        joinColumns = @JoinColumn(name = "account_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Account> friends = new HashSet<>();
    
    @EqualsAndHashCode.Exclude
    @OneToMany
    private Set<FriendRequest> receivedFriendRequests = new HashSet<>();
    
    @EqualsAndHashCode.Exclude
    @OneToMany
    private Set<FriendRequest> sentFriendRequests = new HashSet<>();
}
