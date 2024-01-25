package greencity.repository;

import greencity.entity.User;
import greencity.entity.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    /**
     * Deletes all records from the "events_dates_locations" table associated with a specific event.
     * This operation is performed using a native SQL query.
     *
     * @param eventId The identifier of the event for which the associated date and location records should be deleted.
     */
    @Modifying
    @Query(value = "DELETE FROM events_dates_locations WHERE event_id = :eventId", nativeQuery = true)
    void deleteEventDateLocationsByEventId(Long eventId);

    /**
     * Deletes additional images associated with a specific event.
     * This method performs a native SQL DELETE operation on the "events_images" table,
     * removing all rows where the "event_id" matches the provided event identifier.
     *
     * @param eventId The identifier of the event for which additional images should be deleted.
     */
    @Modifying
    @Query(value = "DELETE FROM events_images WHERE event_id = :eventId", nativeQuery = true)
    void deleteEventAdditionalImagesByEventId(Long eventId);

    /**
     * Counts the number of events associated with the specified organizer.
     *
     * @param organizerId The unique identifier of the organizer for whom the event count is to be obtained.
     * @return The total number of events associated with the specified organizer.
     */
    @Query("select count(*) from Event e where e.organizer.id =:organizerId")
    Long countByOrganizerId(Long organizerId);

    List<Event> findByUsersLikedEvents_id(Long userId);

    List<User> findUsersByUsersLikedEvents_Id(Long eventId);
}
