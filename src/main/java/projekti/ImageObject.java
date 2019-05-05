package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ImageObject extends AbstractPersistable<Long> {
        
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;
    @Column(length=65000)    
    private String description;
    private LocalDateTime datetime;
    @ElementCollection
    private List<Long> likingAccountIds = new ArrayList<>();
    private Integer numberOfLikes = 0;
    
    private String profileId;    
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();       
}
