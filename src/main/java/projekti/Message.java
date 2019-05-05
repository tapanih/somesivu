package projekti;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message extends AbstractPersistable<Long> {

    private String profileId;
    @ManyToOne
    private Account sender;
    @Column(length=65000)
    private String content;
    private LocalDateTime datetime;
    private Integer numberOfLikes = 0;
    @OneToMany
    private List<Reply> replies = new ArrayList<>();
}
