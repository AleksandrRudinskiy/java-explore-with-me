package ru.practicum.explore.user;

import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.participation_request.ParticipationRequest;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addNewUser(UserDto userDto);

    List<UserDto> getUsersByIds(String ids, int from, int size);

    void deleteUserById(long userId);

    ParticipationRequest addParticipationRequest(long userId, Long eventId);

    List<Event> getUserEvents(long userId, int from, int size);

    EventRequestStatusUpdateResult patchRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);

}
