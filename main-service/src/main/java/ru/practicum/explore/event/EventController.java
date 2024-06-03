package ru.practicum.explore.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;


    @GetMapping("/events")
    public List<Event> getEvents(HttpServletRequest request) {


        log.info("GET-request to get all events.");

        return eventService.getEvents(request);
    }
}
