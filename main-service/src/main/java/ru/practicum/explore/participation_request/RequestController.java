package ru.practicum.explore.participation_request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.participation_request.dto.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    public final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequest addParticipationRequest(@PathVariable long userId,
                                                        @RequestParam(defaultValue = "0") Long eventId) {
        log.info("POST add new participation request from userId {} to eventId {}", userId, eventId);
        return requestService.addParticipationRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequestStatus(@PathVariable long userId,
                                                             @PathVariable long eventId,
                                                             @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("PATCH /users/{}/events/{}/requests with body {}", userId, eventId, request);
        return requestService.patchRequestStatus(userId, eventId, request);
    }

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequest> getUsersRequests(@PathVariable long userId) {
        log.info("GET /users/{}/requests", userId);
        return requestService.getUsersRequests(userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequest> getCurrentUsersRequests(@PathVariable long userId,
                                                              @PathVariable long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return requestService.getCurrentUsersRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequest canceledRequest(@PathVariable long userId,
                                                @PathVariable long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.canceledRequest(userId, requestId);
    }
}
