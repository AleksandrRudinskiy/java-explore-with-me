package ru.practicum.explore.participation_request.dto;


import ru.practicum.explore.participation_request.ParticipationRequest;

import java.util.List;

public interface RequestService {
    ParticipationRequest addParticipationRequest(long userId, Long eventId);

    EventRequestStatusUpdateResult patchRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);

    List<ParticipationRequest> getUsersRequests(long userId);

    List<ParticipationRequest> getCurrentUsersRequests(long userId, long eventId);

    ParticipationRequest canceledRequest(long userId, long requestId);
}
