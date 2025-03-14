package greencity.config;

import greencity.annotations.RatingCalculationEnum;
import greencity.client.RestClient;
import greencity.constant.CacheConstants;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.HabitAssign;
import greencity.entity.HabitFactTranslation;
import greencity.entity.User;
import static greencity.enums.EmailNotification.*;
import static greencity.enums.FactOfDayStatus.*;

import greencity.enums.HabitAssignStatus;
import greencity.message.NotificationDto;
import greencity.message.SendHabitNotification;
import greencity.rating.RatingCalculation;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.repository.RatingStatisticsRepo;
import greencity.repository.*;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * Config for scheduling.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Slf4j
@Configuration
@EnableScheduling
@AllArgsConstructor
public class ScheduleConfig {
    private final HabitFactTranslationRepo habitFactTranslationRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final RatingStatisticsRepo ratingStatisticsRepo;
    private final RestClient restClient;
    private final RatingCalculation ratingCalculation;
    private final NewsSubscriberRepo newsSubscriberRepo;
    private final EcoNewsRepo ecoNewsRepo;

    /**
     * Invoke {@link SendHabitNotification} from EmailMessageReceiver to send email
     * letters to each user that hasn't marked any habit during last 3 days.
     *
     * @param users list of potential {@link User} to send notifications.
     */
    private void sendHabitNotificationIfNeed(List<UserVO> users) {
        ZonedDateTime end = ZonedDateTime.now();
        ZonedDateTime start = end.minusDays(3);
        for (UserVO user : users) {
            int count = habitAssignRepo.countMarkedHabitAssignsByUserIdAndPeriod(user.getId(), start, end);
            if (count == 0) {
                restClient.sendHabitNotification(new SendHabitNotification(user.getName(), user.getEmail()));
            }
        }
    }

    /**
     * Every day at 19:00 sends notifications about not marked habits to users with
     * field {@link greencity.enums.EmailNotification} equal to IMMEDIATELY or
     * DAILY.
     */
    @Scheduled(cron = "0 0 19 * * ?", zone = "Europe/Kiev")
    void sendHabitNotificationEveryDay() {
        List<UserVO> users = restClient.findAllByEmailNotification(IMMEDIATELY);
        users.addAll(restClient.findAllByEmailNotification(DAILY));
        sendHabitNotificationIfNeed(users);
    }

    /**
     * Every friday at 19:00 sends notifications about not marked habits to users
     * with field {@link greencity.enums.EmailNotification} equal to WEEKLY.
     */
    @Scheduled(cron = "0 0 19 * * FRI", zone = "Europe/Kiev")
    void sendHabitNotificationEveryWeek() {
        List<UserVO> users = restClient.findAllByEmailNotification(WEEKLY);
        sendHabitNotificationIfNeed(users);
    }

    /**
     * On th 25th of every month at 19:00 sends notifications about not marked
     * habits to users with field {@link greencity.enums.EmailNotification} equal to
     * MONTHLY.
     */
    @Scheduled(cron = "0 0 19 25 * ?", zone = "Europe/Kiev")
    void sendHabitNotificationEveryMonth() {
        List<UserVO> users = restClient.findAllByEmailNotification(MONTHLY);
        sendHabitNotificationIfNeed(users);
    }

    /**
     * Once a day randomly chooses new habitfact of day that has not been habitfact
     * of day during this iteration. factOfDay == 0 - wasn't habitfact of day, 1 -
     * is today's habitfact of day, 2 - already was habitfact of day.
     */
    @CacheEvict(value = CacheConstants.HABIT_FACT_OF_DAY_CACHE, allEntries = true)
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    public void chooseNewHabitFactOfDay() {
        List<HabitFactTranslation> list = habitFactTranslationRepo.findRandomHabitFact();
        if (!list.isEmpty()) {
            habitFactTranslationRepo.updateFactOfDayStatus(CURRENT, USED);
        } else {
            habitFactTranslationRepo.updateFactOfDayStatus(USED, POTENTIAL);
            habitFactTranslationRepo.updateFactOfDayStatus(CURRENT, USED);
            list = habitFactTranslationRepo.findRandomHabitFact();
        }
        habitFactTranslationRepo.updateFactOfDayStatusByHabitFactId(CURRENT, list.get(0).getHabitFact().getId());
    }

    /**
     * Clear habitfact of the day cache at 0:00 am every day.
     */
    @CacheEvict(value = CacheConstants.FACT_OF_THE_DAY_CACHE_NAME, allEntries = true)
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    public void chooseNewHabitFactOfTheDay() {
        // Do nothing to clean cache
    }

    /**
     * Every day at 00:00 deletes from the table rating_statistics records witch are
     * older than period in application properties.
     *
     * @author Dovganyuk Taras
     **/
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    @Transactional
    public void scheduledDeleteRatingStatisticsOlderThan() {
        ratingStatisticsRepo.scheduledDeleteOlderThan();
    }

    /**
     * Every day at 00:00 checks all Assigned Habits and if they are timed out set
     * status EXPIRED.
     *
     * @author Ostap Mykhaylivskii
     **/
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    public void setExpiredStatus() {
        ZonedDateTime now = ZonedDateTime.now();
        List<HabitAssign> habitsInProgress = habitAssignRepo.findAllInProgressHabitAssigns();
        habitsInProgress.forEach(h -> {
            if (h.getCreateDate().plusDays(h.getDuration().longValue()).isBefore(now)) {
                log.info("Set status expired");
                h.setStatus(HabitAssignStatus.EXPIRED);
            } else {
                RatingCalculationEnum ratingCalculationEnum = RatingCalculationEnum.DAY_OF_ECO_HABIT;
                if (h.getCreateDate().toLocalDate().plusDays(14).equals(now.toLocalDate())) {
                    ratingCalculationEnum = RatingCalculationEnum.ACQUIRED_HABIT_14_DAYS;
                } else if (h.getCreateDate().toLocalDate().plusDays(21).equals(now.toLocalDate())) {
                    ratingCalculationEnum = RatingCalculationEnum.ACQUIRED_HABIT_21_DAYS;
                } else if (h.getCreateDate().toLocalDate().plusDays(30).equals(now.toLocalDate())) {
                    ratingCalculationEnum = RatingCalculationEnum.ACQUIRED_HABIT_30_DAYS;
                }
                ratingCalculation.ratingCalculation(ratingCalculationEnum, h.getUser());
            }
        });
    }

    /** Every day at 00:00 sends recent notifications to {@link greencity.entity.NewsSubscriber}.
     * @author Nikita Malov
     **/
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    public void sendInterestingNews() {
        List<EcoNews> ecoNews = ecoNewsRepo.getThreeLastEcoNews();
        newsSubscriberRepo.findAllBy().forEach((email) -> sendNotifications(email, ecoNews));
    }

    private void sendNotifications(String email, List<EcoNews> ecoNews) {
        ecoNews.forEach((news) ->
            restClient.sendNotification(new NotificationDto(news.getTitle(), news.getText()), email));
    }
}
