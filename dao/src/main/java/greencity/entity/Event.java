package greencity.entity;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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

}
