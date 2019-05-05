package projekti;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageObjectRepository extends JpaRepository<ImageObject, Long> {
    public List<ImageObject> findByProfileId(String profileId, Pageable pageable);
}
