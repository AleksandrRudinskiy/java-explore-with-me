package ru.practicum.explore.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.common.ConflictException;
import ru.practicum.explore.common.IncorrectRequestException;
import ru.practicum.explore.common.NotFoundException;
import ru.practicum.explore.common.State;
import ru.practicum.explore.event.EventRepository;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.participation_request.ParticipationRequest;
import ru.practicum.explore.participation_request.ParticipationRequestRepository;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;

    @Override
    public UserDto addNewUser(UserDto userDto) {
        String email = userDto.getEmail();
        String[] parts = email.split("@");
        String[] emailDomainParts = parts[1].split("\\.");
        String localPart = parts[0];
        String domainPart = emailDomainParts[0];
        if (localPart.length() > 64 || domainPart.length() > 63) {
            throw new IncorrectRequestException("werfwefwefw");
        }


        User user = userRepository.save(UserMapper.convertToUser(userDto));
        log.info("Created New user with userId = {}", user.getId());
        return UserMapper.convertToUserDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(String ids, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new RuntimeException("Параметр from не должен быть меньше 1");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (ids == null) {
            return userRepository.findAll(page).stream()
                    .map(UserMapper::convertToUserDto)
                    .collect(Collectors.toList());
        } else {
            String[] arrayStrIds = ids.split(",");
            List<Long> userIds = new ArrayList<>();
            for (String strId : arrayStrIds) {
                userIds.add(Long.parseLong(strId));
            }
            List<User> users = userRepository.selectUsers(userIds, page);
            return users.stream()
                    .map(UserMapper::convertToUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found.");
        }
        userRepository.deleteById(userId);
    }

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
    public List<Event> getUserEvents(long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new RuntimeException("Параметр from не должен быть меньше 1");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventRepository.findEventsByInitiatorId(userId, page);
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
