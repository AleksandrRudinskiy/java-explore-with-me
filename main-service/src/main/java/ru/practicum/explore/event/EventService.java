package ru.practicum.explore.event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<Event> getEvents(HttpServletRequest request);
}
