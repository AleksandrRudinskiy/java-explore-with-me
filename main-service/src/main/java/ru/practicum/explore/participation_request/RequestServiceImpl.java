package ru.practicum.explore.participation_request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.common.ConflictException;
import ru.practicum.explore.common.IncorrectRequestException;
import ru.practicum.explore.common.NotFoundException;
import ru.practicum.explore.common.State;
import ru.practicum.explore.event.EventRepository;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.participation_request.dto.RequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequest addParticipationRequest(long userId, Long eventId) {
        log.info("NEXT addParticipationRequest FROM UserServiceImpl");
        if (eventId == 0) {
            throw new IncorrectRequestException("Failed to convert value of type java.lang.String to required type long; nested exception is java.lang.NumberFormatException: For input string: ad");
        }
        Event event = eventRepository.findEventById(eventId);
        int confirmedRequestsCount = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();
        log.info("CONFIRMED REQUESTS (eventId {}): {}", eventId, confirmedRequestsCount);
        log.info("PARTICIPANT LIMIT (eventId {}): {}", eventId, participantLimit);
        if (confirmedRequestsCount == participantLimit && participantLimit != 0) {
            throw new ConflictException("wefwefwe");
        }
        if (event.getInitiator().getId() == userId || event.getState() != State.PUBLISHED) {
            throw new ConflictException("wjknefwkjenfw");
        }
        LocalDateTime now = LocalDateTime.now();
        ParticipationRequest participationRequest = new ParticipationRequest(
                0L, now.format(formatter), eventId, userId, State.PENDING);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            participationRequest.setStatus(State.CONFIRMED);
            confirmedRequestsCount++;
            event.setConfirmedRequests(confirmedRequestsCount);
        }
        ParticipationRequest participationRequest1 = participationRequestRepository.save(participationRequest);
        log.info("Added NEW participation request with body {}", participationRequest1);
        return participationRequest1;
    }


    @Override
    public EventRequestStatusUpdateResult patchRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        List<Long> requestsIds = request.getRequestIds();
        String patchedStatus = request.getStatus();
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        for (Long requestsId : requestsIds) {
            ParticipationRequest participationRequest = participationRequestRepository.findById(requestsId).get();
            participationRequest.setStatus(State.valueOf(patchedStatus));
            participationRequestRepository.save(participationRequest);
            Event event = eventRepository.findEventById(eventId);
            int confirmedRequestsCount = event.getConfirmedRequests();
            int participantLimit = event.getParticipantLimit();
            if (confirmedRequestsCount == participantLimit && participantLimit != 0) {
                throw new ConflictException("wefwefwe");
            }
            confirmedRequestsCount++;
            event.setConfirmedRequests(confirmedRequestsCount);
            eventRepository.save(event);

            if (State.valueOf(patchedStatus).equals(State.CONFIRMED)) {
                confirmedRequests.add(participationRequest);
            } else {
                rejectedRequests.add(participationRequest);
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<ParticipationRequest> getUsersRequests(long userId) {
        return participationRequestRepository.findByRequesterId(userId);
    }

    @Override
    public List<ParticipationRequest> getCurrentUsersRequests(long userId, long eventId) {
        //зачем UserId ? может стоит проверять кажное найденное событие на requesterId ?
        return participationRequestRepository.findByEventId(eventId);
    }

    @Override
    public ParticipationRequest canceledRequest(long userId, long requestId) {
        if (!participationRequestRepository.existsById(requestId)) {
            throw new NotFoundException("flkwmeflwef");
        }
        ParticipationRequest request = participationRequestRepository.findById(requestId).get();
        request.setStatus(State.CANCELED);
        log.info("request = {}", request);
        return request;
    }
}
