package ru.practicum.explore.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.common.State;
import ru.practicum.explore.location.Location;
import ru.practicum.explore.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "events", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;
    @Column(name = "created_on")
    private String createdOn;
    private String description;
    @Column(name = "event_date")
    private String eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    @ToString.Exclude
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    @ToString.Exclude
    private Location location;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private String publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;
    private String title;
    private Integer views;

}
