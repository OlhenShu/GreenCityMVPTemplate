package greencity.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "notified_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotifiedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Notification notification;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private Boolean isRead;
}