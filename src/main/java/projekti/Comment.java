package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
class Comment extends AbstractPersistable<Long> {
    
    private Long parentId;
    @ManyToOne
    private Account sender;
    @Column(length=65000)
    private String content;
    private LocalDateTime datetime;
    @ElementCollection
    private List<Long> likingAccountIds = new ArrayList<>();
    private Integer numberOfLikes = 0;    
}
