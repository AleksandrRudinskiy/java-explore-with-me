package ru.practicum.explore.event;

import ru.practicum.explore.event.dto.EventDto;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface EventService {
    List<Event> getEvents(
            String text, String categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable,
            String sort, int from, int size, HttpServletRequest request);

    Event addEvent(long userId, EventDto eventDto);

    List<EventFullDto> searchEvents(String users, String states, String categories, String rangeStart, String rangeEnd, int from, int size);

    Event getEventInfo(long eventId);

    Event getEventById(long eventId, HttpServletRequest request) throws IOException;

    Event patchEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Event patchEventByUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<Event> getUserEvents(long userId, int from, int size);

}
