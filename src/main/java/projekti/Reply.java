package projekti;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply extends AbstractPersistable<Long> {
    
    private Long parentId;
    private String profileId;
    @ManyToOne
    private Account sender;
    @Column(length=65000)
    private String content;
    private LocalDateTime datetime;
    private Integer numberOfLikes = 0;   
}
