package ru.practicum.explore.event;

import ru.practicum.explore.event.dto.EventDto;
import ru.practicum.explore.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.participation_request.ParticipationRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface EventService {
    List<Event> getEvents(HttpServletRequest request, String rangeStart, String rangeEnd, int from, int size);

    Event addEvent(long userId, EventDto eventDto);

    List<Event> searchEvents(
            String users, String states, String categories, String rangeStart, String rangeEnd, int from, int size);

    Event getEventInfo(long eventId);

    Event getEventById(long eventId, HttpServletRequest request) throws IOException;

    Event patchEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<ParticipationRequest> getAllRequests();

}
