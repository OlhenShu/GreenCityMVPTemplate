package greencity.entity;

import greencity.enums.NotificationSourceType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @CreationTimestamp
    private ZonedDateTime creationDate;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private NotificationSourceType sourceType;

    @Column(nullable = false)
    private Long sourceId;

    @OneToMany(mappedBy = "notification")
    private List<NotifiedUser> notifiedUsers;
}