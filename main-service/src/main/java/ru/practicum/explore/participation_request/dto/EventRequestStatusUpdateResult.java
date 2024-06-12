package ru.practicum.explore.participation_request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explore.participation_request.ParticipationRequest;

import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequest> confirmedRequests;
    private List<ParticipationRequest> rejectedRequests;
}
