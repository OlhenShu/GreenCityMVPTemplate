package greencity.repository;

import greencity.entity.event.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
}