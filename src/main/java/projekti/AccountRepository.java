package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    boolean existsByUsername(String username);
    List<Account> findByNameContainingIgnoreCase(String name);
}
