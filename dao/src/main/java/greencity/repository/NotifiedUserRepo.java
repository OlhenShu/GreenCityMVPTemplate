package greencity.repository;

import greencity.entity.NotifiedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifiedUserRepo extends JpaRepository<NotifiedUser, Long> {
}