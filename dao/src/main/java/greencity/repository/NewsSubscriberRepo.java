package greencity.repository;

import greencity.entity.NewsSubscriber;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsSubscriberRepo extends JpaRepository<NewsSubscriber, Long> {
    @Query("select ns.email from NewsSubscriber ns")
    List<String> findAllBy();

    Boolean existsByEmail(String email);
}
