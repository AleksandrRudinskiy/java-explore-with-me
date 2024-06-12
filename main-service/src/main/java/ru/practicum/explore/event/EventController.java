package ru.practicum.explore.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventDto;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;

    @GetMapping("/admin/events")
    public List<EventFullDto> searchEvents(@RequestParam(required = false) String users,
                                           @RequestParam(required = false) String states,
                                           @RequestParam(required = false) String categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("GET/admin/events with params: " +
                        "users {}, states {},  categories {}, rangeStart {}, rangeEnd {}, from {}, size {} ",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(required = false) String text,
                                 @RequestParam(required = false) String categories,
                                 @RequestParam(required = false) Boolean paid,
                                 @RequestParam(required = false) String rangeStart,
                                 @RequestParam(required = false) String rangeEnd,
                                 @RequestParam(required = false) Boolean onlyAvailable,
                                 @RequestParam(required = false) String sort,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size,
                                 HttpServletRequest request) {
        log.info("GET-request to find all events with text = {}; start {}; end: {}", text, rangeStart, rangeEnd);
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/events/{eventId}")
    public Event getEventById(@PathVariable long eventId, HttpServletRequest request) throws IOException {
        log.info("GET event by eventId {}", eventId);
        return eventService.getEventById(eventId, request);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Event addEvent(@PathVariable long userId,
                          @RequestBody @Valid EventDto eventDto) {
        log.info("POST /users/{}/events with body : {}", userId, eventDto);
        return eventService.addEvent(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public Event getEventInfo(@PathVariable long userId,
                              @PathVariable long eventId) {
        return eventService.getEventInfo(eventId);
    }

    @PatchMapping("/admin/events/{eventId}")
    public Event patchEvent(@PathVariable long eventId,
                            @RequestBody @Valid UpdateEventAdminRequest eventDto) {
        log.info("PATCH /admin/events/{} with body: {}", eventId, eventDto);
        return eventService.patchEvent(eventId, eventDto);
    }


    @PatchMapping("/users/{userId}/events/{eventId}")
    public Event patchEventByCurrentUser(@PathVariable long userId,
                                         @PathVariable long eventId,
                                         @RequestBody @Valid UpdateEventUserRequest eventDto) {
        log.info("PATCH event by current user {} with id {} body {}", userId, eventId, eventDto);
        return eventService.patchEventByUser(userId, eventId, eventDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<Event> addEvent(@PathVariable long userId,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("GET user events from userId: {}", userId);
        return eventService.getUserEvents(userId, from, size);
    }


}
