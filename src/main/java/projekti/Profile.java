package projekti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="owner")
public class Profile implements Serializable {
    
    @Id
    private String id;
    
    @OneToOne(mappedBy = "profile")
    private Account owner;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();    
}
