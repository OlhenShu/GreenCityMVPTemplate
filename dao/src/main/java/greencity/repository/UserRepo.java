package greencity.repository;

import greencity.dto.habit.HabitVO;
import greencity.dto.user.RecommendFriendDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.repository.options.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Find {@link User} by email.
     *
     * @param email user email.
     * @return {@link User}
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all {@link UserManagementVO}.
     *
     * @param filter   filter parameters
     * @param pageable pagination
     * @return list of all {@link UserManagementVO}
     */
    @Query(" SELECT new greencity.dto.user.UserManagementVO(u.id, u.name, u.email, u.userCredo, u.role, u.userStatus) "
        + " FROM User u ")
    Page<UserManagementVO> findAllManagementVo(UserFilter filter, Pageable pageable);

    /**
     * Find not 'DEACTIVATED' {@link User} by email.
     *
     * @param email - {@link User}'s email
     * @return found {@link User}
     * @author Vasyl Zhovnir
     */
    @Query("FROM User WHERE email=:email AND userStatus <> 1")
    Optional<User> findNotDeactivatedByEmail(String email);

    /**
     * Find id by email.
     *
     * @param email - User email
     * @return User id
     * @author Zakhar Skaletskyi
     */
    @Query("SELECT id FROM User WHERE email=:email")
    Optional<Long> findIdByEmail(String email);

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link User}'s id
     * @param userLastActivityTime - new {@link User}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE User SET last_activity_time=:userLastActivityTime WHERE id=:userId")
    void updateUserLastActivityTime(Long userId, Date userLastActivityTime);

    /**
     * Updates user status for a given user.
     *
     * @param userId     - {@link User}'s id
     * @param userStatus {@link String} - string value of user status to set
     */
    @Modifying
    @Transactional
    @Query("UPDATE User SET userStatus = CASE "
        + "WHEN (:userStatus = 'DEACTIVATED') THEN 1 "
        + "WHEN (:userStatus = 'ACTIVATED') THEN 2 "
        + "WHEN (:userStatus = 'CREATED') THEN 3 "
        + "WHEN (:userStatus = 'BLOCKED') THEN 4 "
        + "ELSE 0 END "
        + "WHERE id = :userId")
    void updateUserStatus(Long userId, String userStatus);

    /**
     * Find the last activity time by {@link User}'s id.
     *
     * @param userId - {@link User}'s id
     * @return {@link Date}
     */
    @Query(nativeQuery = true,
        value = "SELECT last_activity_time FROM users WHERE id=:userId")
    Optional<Timestamp> findLastActivityTimeById(Long userId);

    /**
     * Updates user rating as event organizer.
     *
     * @param userId {@link User}'s id
     * @param rate   new {@link User}'s rating as event organizer
     * @author Danylo Hlynskyi
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE User SET eventOrganizerRating=:rate WHERE id=:userId")
    void updateUserEventOrganizerRating(Long userId, Double rate);

    /**
     * Retrieves the list of the user's friends (which have INPROGRESS assign to the
     * habit).
     *
     * @param habitId {@link HabitVO} id.
     * @param userId  {@link UserVO} id.
     * @return List of friends.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM ((SELECT user_id FROM users_friends AS uf "
        + "WHERE uf.friend_id = :userId AND uf.status = 'FRIEND' AND "
        + "(SELECT count(*) FROM habit_assign ha WHERE ha.habit_id = :habitId AND ha.user_id = uf.user_id "
        + "AND ha.status = 'INPROGRESS') = 1) "
        + "UNION "
        + "(SELECT friend_id FROM users_friends AS uf "
        + "WHERE uf.user_id = :userId AND uf.status = 'FRIEND' AND "
        + "(SELECT count(*) FROM habit_assign ha WHERE ha.habit_id = :habitId AND ha.user_id = uf.friend_id "
        + "AND ha.status = 'INPROGRESS') = 1)) as ui JOIN users as u ON user_id = u.id")
    List<User> getFriendsAssignedToHabit(Long userId, Long habitId);

    /**
     * Get all user friends.
     *
     * @param userId The ID of the user.
     *
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id IN ( "
        + "(SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')"
        + "UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'));")
    List<User> getAllUserFriends(Long userId);

    /**
     * Retrieves a filtered list of users and their friend-related details based on specified criteria.
     *
     * @param nameCriteria      The criteria for filtering user's names.
     * @param city              The city for filtering users.
     * @param hasMutualFriends  Flag indicating to include users only with mutual friends.
     * @param pageable          Pagination information for the resulting list.
     * @param userId            The unique identifier of the user initiating the query.
     * @return                  A paginated list of {@link UserFriendDto} containing user details.
     * @author Denys Liubchenko
     */
    @Query("SELECT new greencity.dto.user.UserFriendDto ("
        + "u.id ,u.city, COUNT(uc), u.name, u.profilePicturePath, u.rating) "
        + "FROM User u LEFT JOIN u.connections uc "
        + "WHERE (uc.friend.id IN "
        + "(SELECT u2c.friend.id FROM User u2 "
        + "LEFT JOIN u2.connections u2c WHERE u2.id = :userId AND u2c.status = 'FRIEND') "
        + "OR uc.friend.id IS NULL) "
        + "AND u.id != :userId  "
        + "AND (:nameCriteria IS NULL OR u.name LIKE :nameCriteria) "
        + "AND (:city IS NULL OR u.city = :city)"
        + "GROUP BY u.id HAVING (:hasMutualFriends IS FALSE OR COUNT(uc) > 0)")
    Page<UserFriendDto> findAllUserFriendDtoByFriendFilter(String nameCriteria, String city, Boolean hasMutualFriends,
                                                           Pageable pageable, Long userId);

    /**
     * Sends a friend request from one user to another.
     *
     * @param userId            The unique identifier of the user initiating the query.
     * @param friendId          The unique identifier of the friend to sent request.
     *
     * @author Denys Liubchenko
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO users_friends "
        + "(user_id, friend_id, status, created_date) VALUES (:userId, :friendId, 'REQUEST', NOW());")
    void addFriend(Long userId, Long friendId);

    /**
     * Checks if users have friend connection.
     *
     * @param userId            The unique identifier of the user initiating the query.
     * @param friendId          The unique identifier of the friend to sent request.
     */
    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT 1 FROM users_friends "
        + "WHERE ((user_id = :userId AND friend_id = :friendId) OR (user_id = :friendId AND friend_id = :userId)) "
        + "AND status = 'FRIEND')")
    boolean isFriend(Long userId, Long friendId);

    /**
     * Delete friend connection between users.
     *
     * @param userId            The unique identifier of the user initiating the query.
     * @param friendId          The unique identifier of the friend to sent request.
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM users_friends WHERE (user_id = :userId AND friend_id = :friendId)"
        + " OR (user_id = :friendId AND friend_id = :userId)")
    void deleteUserFriend(Long userId, Long friendId);

    /**
     * Retrieves a paginated list of recommended friends for a given user, considering mutual habits and mutual friends,
     * ordered by the count of mutual friends in descending order, followed by the prioritized city, and then
     * by the count of mutual habits in descending order.
     *
     * @param userId   The ID of the user for whom friend recommendations are sought.
     * @param pageable Pageable object defining the page size, page number, and sorting criteria for the result set.
     * @param city     The city used for prioritizing recommendations.
     * @return A paginated list of RecommendFriendDto objects representing recommended friends.
     */
    @Query(value = "SELECT new greencity.dto.user.RecommendFriendDto(u.id,u.city,u.name,u.profilePicturePath,u.rating,"
        + "(SELECT count(ha1.habit.id) from HabitAssign ha1 WHERE ha1.user.id = :userId "
        + "AND (ha1.status = 'INPROGRESS' OR ha1.status = 'ACQUIRED') AND ha1.habit.id IN "
        + "(SELECT ha2.habit.id from HabitAssign ha2 WHERE (ha2.user.id = u.id))) as mutualHabits, "
        + "(SELECT COUNT(uf1.friend.id) from UserFriend uf1 JOIN UserFriend uf2 ON uf1.friend.id = uf2.friend.id "
        + "WHERE uf1.user.id = :userId AND uf2.user.id = u.id AND uf1.status = 'FRIEND' "
        + "AND uf2.status = 'FRIEND') as mutualFriends)"
        + "FROM User u WHERE u.id != :userId AND NOT EXISTS "
        + "(SELECT 1 FROM User u4 LEFT JOIN UserFriend uc ON u4.id = uc.user.id "
        + "WHERE uc.friend.id = :userId AND u.id = u4.id)"
        + "GROUP BY u.id, u.city, u.name, u.profilePicturePath, u.rating ORDER BY mutualFriends DESC, "
        + "CASE WHEN u.city = :city THEN 0 ELSE 1 END, mutualHabits DESC",
           countQuery = "SELECT COUNT(*) FROM User u WHERE u.id != :userId AND NOT EXISTS "
        + "(SELECT 1 FROM User u4 LEFT JOIN UserFriend uc ON u4.id = uc.user.id "
        + "WHERE uc.friend.id = :userId AND u.id = u4.id)")
    Page<RecommendFriendDto> findAllRecommendedFriends(Long userId,Pageable pageable,String city);
}