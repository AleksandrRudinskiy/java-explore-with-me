package ru.practicum.explore.event;

import endpoint.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.explore.category.CategoryRepository;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.common.*;
import ru.practicum.explore.endpoint.StatsClient;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.location.Location;
import ru.practicum.explore.location.LocationRepository;
import ru.practicum.explore.user.UserRepository;
import ru.practicum.explore.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> searchEvents(String users, String states, String categories, String rangeStart, String rangeEnd, int from, int size) {
        PageRequest page = checkPageableParameters(from, size);
        List<String> statesList = parseStates(states);
        List<Long> usersList = parseUsersIds(users);
        List<Long> categoriesList = parseCategoriesIds(categories);
        List<Event> events = new ArrayList<>();
        if (users == null && states == null && categories == null) {
            events.addAll(eventRepository.findAll(page).toList());
            log.info("searched events =  {}", events);
            return events.stream()
                    .map(EventMapper::convertToEventFullDto)
                    .collect(Collectors.toList());
        }
        events.addAll(eventRepository.searchEventsByAdmin(statesList, usersList, categoriesList, page));
        log.info("searched events =  {}", events);

        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);
            return events.stream().filter(e -> LocalDateTime.parse(e.getEventDate(), formatter).isAfter(start)
                            && LocalDateTime.parse(e.getEventDate(), formatter).isBefore(end)).collect(Collectors.toList())
                    .stream()
                    .map(EventMapper::convertToEventFullDto)
                    .collect(Collectors.toList());
        }
        log.info("searched events =  {}", events);
        return events.stream()
                .map(EventMapper::convertToEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEvents(
            String text, String categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable,
            String sort, int from, int size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);
            if (!end.isAfter(start)) {
                throw new IncorrectRequestException("Event must be published");
            }
        }
        saveStats(request);
        List<Event> events = eventRepository.findAll();
        log.info("events = {}", events);
        if (text != null) {
            events = events.stream()
                    .filter(event -> event.getAnnotation().contains(text) || event.getDescription().contains(text)).collect(Collectors.toList());
        }
        if (paid != null && paid) {
            log.info("events = {}", events);
            events = events.stream()
                    .filter(Event::getPaid)
                    .collect(Collectors.toList());
        }
        if (sort != null && sort.equals("EVENT_DATE")) {
            log.info("events = {}", events);
            return events.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .map(EventMapper::convertToEventFullDto)
                    .collect(Collectors.toList());
        }
        if (sort != null && sort.equals("VIEWS")) {
            log.info("events = {}", events);
            return events.stream()
                    .sorted(Comparator.comparingLong(Event::getViews))
                    .map(EventMapper::convertToEventFullDto)
                    .collect(Collectors.toList());
        }
        log.info("events = {}", events);
        return events.stream()
                .map(EventMapper::convertToEventFullDto)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public Event addEvent(long userId, EventDto eventDto) {
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), formatter);
        LocalDateTime currentDate = LocalDateTime.now();
        long duration = Duration.between(currentDate, eventDate).getSeconds();

        if (duration < 7200) {
            throw new NotValidEventDateException(
                    "event date and time cannot be earlier than two hours from the current moment");
        }
        Location location = locationRepository.save(
                new Location(0L, eventDto.getLocation().getLat(), eventDto.getLocation().getLon()));
        User initiator = userRepository.findById(userId).get();
        Category category = categoryRepository.findById(eventDto.getCategoryId()).get();
        if (eventDto.getPaid() == null) {
            eventDto.setPaid(false);
        }
        if (eventDto.getParticipantLimit() == null) {
            eventDto.setParticipantLimit(0);
        }
        if (eventDto.getRequestModeration() == null) {
            eventDto.setRequestModeration(true);
        }
        return eventRepository.save(
                EventMapper.convertToEvent(eventDto, initiator, category, location));
    }

    @Override
    public Event getEventInfo(long eventId) {
        return eventRepository.findEventById(eventId);
    }


    @Override
    public Event getEventById(long eventId, HttpServletRequest request) {
        checkExists(eventId);
        saveStats(request);
        Event event = eventRepository.findEventById(eventId);
        ResponseEntity<Object> response = statsClient.getStats("2020-01-01 00:00:00", "2035-01-01 00:00:00", request.getRequestURI(), true);
        String viewStatsStr = Objects.requireNonNull(response.getBody()).toString();
        int index = viewStatsStr.indexOf("hits");
        String subString = viewStatsStr.substring(index);
        String[] arrayStr = subString.split("=");
        String[] arrayNext = arrayStr[1].split("[,}]");
        long views = Long.parseLong(arrayNext[0]);
        event.setViews(views);
        return eventRepository.save(event);
    }


    @Override
    public Event patchEvent(long eventId, UpdateEventAdminRequest eventRequest) {
        Event oldEvent = eventRepository.findEventById(eventId);
        String state = oldEvent.getState().toString();
        String stateAction = eventRequest.getStateAction();
        if (state.equals(State.CANCELED.toString())) {
            throw new ConflictException("Event with id " + eventId + " already " + state);
        }
        if (state.equals(State.PENDING.toString()) || !state.equals(State.PUBLISHED.toString())) {
            updateEventRequest(oldEvent, eventRequest);
            if (stateAction != null && stateAction.equals(StateAction.PUBLISH_EVENT.toString())) {
                oldEvent.setState(State.PUBLISHED);
            }
            if (stateAction != null && stateAction.equals(StateAction.REJECT_EVENT.toString())) {
                oldEvent.setState(State.CANCELED);
            }
        } else {
            throw new ConflictException("Event with id " + eventId + " already " + state);
        }
        return eventRepository.save(oldEvent);
    }

    @Override
    public Event patchEventByUser(long userId, long eventId, UpdateEventUserRequest eventRequest) {
        Event oldEvent = eventRepository.findEventById(eventId);
        String oldEventState = oldEvent.getState().toString();
        String stateAction = eventRequest.getStateAction();
        if (oldEventState.equals(State.CANCELED.toString()) || oldEventState.equals(State.PENDING.toString())) {
            updateEventRequest(oldEvent, eventRequest);
            if (eventRequest.getEventDate() != null) {
                oldEvent.setState(State.valueOf(eventRequest.getStateAction()));
            }
            if (stateAction != null && stateAction.equals(StateAction.SEND_TO_REVIEW.toString())) {
                oldEvent.setState(State.PENDING);
            }
            if (stateAction != null && stateAction.equals(StateAction.CANCEL_REVIEW.toString())) {
                oldEvent.setState(State.CANCELED);
            }
        } else {
            throw new ConflictException("Imposible to modify status of event ");
        }
        log.info("PATCHED EVENT BY USER : {} ", oldEvent);
        return eventRepository.save(oldEvent);
    }

    @Override
    public List<Event> getUserEvents(long userId, int from, int size) {
        PageRequest page = checkPageableParameters(from, size);
        return eventRepository.findEventsByInitiatorId(userId, page);
    }

    private PageRequest checkPageableParameters(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new RuntimeException("Параметр from не должен быть меньше 1");
        }
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    private List<String> parseStates(String states) {
        List<String> statesList = new ArrayList<>();
        if (states != null) {
            String[] statesStr = states.split(",");
            statesList = List.of(statesStr);
        }
        return statesList;
    }

    private List<Long> parseUsersIds(String users) {
        List<Long> usersList = new ArrayList<>();
        if (users != null) {
            String[] usersStr = users.split(",");
            for (String strUser : usersStr) {
                usersList.add(Long.parseLong(strUser));
            }
        }
        return usersList;
    }

    private List<Long> parseCategoriesIds(String categories) {
        List<Long> categoriesList = new ArrayList<>();
        if (categories != null) {
            String[] categoriesStr = categories.split(",");
            for (String strCategoryId : categoriesStr) {
                categoriesList.add(Long.parseLong(strCategoryId));
            }
        }
        return categoriesList;
    }

    private void saveStats(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                0L, "main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(formatter));
        statsClient.saveEndpointHit(endpointHitDto);
    }

    private void checkExists(long eventId) {
        if (!eventRepository.existsById(eventId)
                || eventRepository.findById(eventId).get().getState().equals(State.PENDING)
                || eventRepository.findById(eventId).get().getState().equals(State.CANCELED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    private void updateEventRequest(Event oldEvent, UpdateEventUserRequest updateEventRequest) {
        if (updateEventRequest.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateEventRequest.getCategoryId()).get();
            oldEvent.setCategory(category);
        }
        if (updateEventRequest.getDescription() != null) {
            oldEvent.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventRequest.getEventDate(), formatter);
            LocalDateTime currentDate = LocalDateTime.now();
            if (eventDate.isBefore(currentDate)) {
                throw new NotValidEventDateException("date cannot be in the past");
            }
            oldEvent.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getLocation() != null) {
            Location location = locationRepository.save(new Location(0L, updateEventRequest.getLocation().getLat(), updateEventRequest.getLocation().getLon()));
            oldEvent.setLocation(location);
        }
        if (updateEventRequest.getPaid() != null) {
            oldEvent.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.getTitle() != null) {
            oldEvent.setTitle(updateEventRequest.getTitle());
        }
    }

}
