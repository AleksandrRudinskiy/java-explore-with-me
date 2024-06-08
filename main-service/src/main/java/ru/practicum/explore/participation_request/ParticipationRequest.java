package ru.practicum.explore.participation_request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.common.State;

import javax.persistence.*;

@Entity
@Table(name = "requests", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String created;
    @Column(name = "event_id")
    @JsonProperty("event")
    private long eventId;
    @Column(name = "requester_id")
    @JsonProperty("requester")
    private long requesterId;
    private State status;
}
