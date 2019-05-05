package projekti;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryRepository extends JpaRepository<Gallery, String> {
    public Optional<Gallery> findByProfileId(String profileId);
}
