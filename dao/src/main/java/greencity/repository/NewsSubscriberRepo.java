package greencity.repository;

import greencity.entity.NewsSubscriber;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsSubscriberRepo extends JpaRepository<NewsSubscriber, Long> {
    /**
     * Retrieves a list of all email addresses of news subscribers.
     *
     * @return A list of email addresses of all news subscribers.
     */
    @Query("select ns.email from NewsSubscriber ns")
    List<String> findAllBy();

    /**
     * Checks if a news subscriber with the given email address exists.
     *
     * @param email The email address to check for existence.
     * @return true if a news subscriber with the given email address exists, false otherwise.
     */
    Boolean existsByEmail(String email);
}
