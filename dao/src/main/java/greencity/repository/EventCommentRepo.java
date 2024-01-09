package greencity.repository;

import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {


    /**
     * The method returns not deleted comment {@link EventComment}, specified by id.
     *
     * @param id id of {@link EventComment} parent comment
     * @return not deleted comment by it id
     */
    Optional<EventComment> findByIdAndStatusNot(Long id, CommentStatus status);

    /**
     * Method returns all {@link EventComment} by page.
     *
     * @param pageable page of news.
     * @param eventId  id of {@link Event} for which comments we search.
     * @return all active {@link EventComment} by page.
     */
    Page<EventComment> findAllByParentCommentIdIsNullAndEventIdAndStatusNotOrderByCreatedDateDesc(Pageable pageable,
                                                                                                  Long eventId, CommentStatus status);
}