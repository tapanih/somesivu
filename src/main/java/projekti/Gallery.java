package projekti;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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
@EqualsAndHashCode(exclude="owner")
public class Gallery extends AbstractPersistable<Long> {
    
    private String profileId;
    
    @OneToOne(mappedBy = "gallery")
    private Account owner;   
    
    @ElementCollection
    private Set<Long> commentIds = new HashSet<>(); 
        
    @OneToMany(cascade = CascadeType.ALL)
    private List<ImageObject> images = new ArrayList<>();  
}
