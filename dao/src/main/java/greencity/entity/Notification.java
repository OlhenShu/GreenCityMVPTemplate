package greencity.entity;

import java.time.ZonedDateTime;
import java.util.Set;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToMany
    @JoinTable(
        name = "notifications_users",
        joinColumns = @JoinColumn(name = "notification_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> receivers;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String shortDescription;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false)
    @CreationTimestamp
    private ZonedDateTime creationDate;
}
