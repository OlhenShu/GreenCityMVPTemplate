package greencity.repository;

import greencity.entity.User;
import greencity.entity.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    /**
     * Method for getting all events.
     *
     * @return list of {@link Event} instances.
     */
    Page<Event> findAllByOrderByIdDesc(Pageable page);

    /**
     * Method for getting all events by user.
     *
     * @return list of {@link Event} instances.
     */
    @Query(value = "SELECT e FROM Event e LEFT JOIN e.attenders AS att WHERE att.id = :userId")
    List<Event> findAllByAttender(Long userId);

    /**
     * Method for getting events created by User.
     *
     * @return list of {@link Event} instances.
     */
    @Query(value = "select e from Event e where e.organizer.id =:userId")
    Page<Event> findEventsByOrganizer(Pageable page, Long userId);

    /**
     * Method for getting pages of users events and events which were created by
     * this user.
     *
     * @return list of {@link Event} instances.
     */
    @Query(
            value = "select distinct e from Event e LEFT JOIN e.attenders AS att "
                    + "WHERE e.organizer.id =:userId OR att.id = :userId ORDER BY e.id DESC")
    Page<Event> findRelatedEventsByUser(Pageable page, Long userId);

    /**
     * Get all events by event organizer.
     *
     * @param organizer {@link User}.
     */
    List<Event> getAllByOrganizer(User organizer);

    /**
     * Get all user's favorite events by user id.
     *
     * @param userId {@link Long}.
     */
    @Query(value = "SELECT e FROM Event e LEFT JOIN e.followers AS f WHERE f.id = :userId")
    Page<Event> findAllFavoritesByUser(Long userId, Pageable pageable);

    /**
     * Get subscribed events in given event ids by user id.
     */
    @Query(value = "SELECT e FROM Event e LEFT JOIN e.attenders AS f WHERE e.id in :eventIds AND f.id = :userId")
    List<Event> findSubscribedAmongEventIds(Collection<Long> eventIds, Long userId);

    /**
     * Get favorite events in given events by user id.
     */
    @Query(value = "SELECT e FROM Event e LEFT JOIN e.followers AS f WHERE e.id in :eventIds AND f.id = :userId")
    List<Event> findFavoritesAmongEventIds(Collection<Long> eventIds, Long userId);

}