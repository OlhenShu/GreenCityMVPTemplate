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

    /**
     * Retrieves a list of events liked by a user based on the user's ID.
     *
     * @param userId The ID of the user for whom liked events are retrieved.
     * @return A list of Event objects representing events liked by the user.
     */
    List<Event> findByUsersLikedEvents_id(Long userId);

    /**
     * Retrieves a list of users who have liked a specific event based on the event's ID.
     *
     * @param eventId The ID of the event for which users who liked the event are retrieved.
     * @return A list of User objects representing users who have liked the event.
     */
    List<User> findUsersByUsersLikedEvents_Id(Long eventId);
}
