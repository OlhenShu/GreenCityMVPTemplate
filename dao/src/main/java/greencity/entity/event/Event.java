package greencity.entity.event;

import greencity.entity.Tag;
import greencity.entity.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OrderBy;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String title;

    @ManyToOne
    @JoinColumn(name = "organizer_id", referencedColumnName = "id")
    private User organizer;

    @Column
    private LocalDate creationDate;

    @Column
    @NotNull
    private String description;

    @Column
    private String titleImage;

    @NotNull
    @OrderBy("finishDate ASC")
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventDateLocation> dates = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventImages> additionalImages = new ArrayList<>();

    @Column
    private Boolean open = true;

    @ManyToMany
    @JoinTable(name = "events_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @ManyToMany
    @JoinTable(
            name = "events_users_likes",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id"))
    private Set<User> usersLikedEvents = new HashSet<>();
}
