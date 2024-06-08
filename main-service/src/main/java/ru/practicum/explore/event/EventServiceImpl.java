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
import ru.practicum.explore.event.dto.EventDto;
import ru.practicum.explore.event.dto.EventMapper;
import ru.practicum.explore.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.location.Location;
import ru.practicum.explore.location.LocationRepository;
import ru.practicum.explore.participation_request.ParticipationRequest;
import ru.practicum.explore.participation_request.ParticipationRequestRepository;
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
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient statsClient;


    @Override
    public List<Event> getEvents(HttpServletRequest request, String rangeStart, String rangeEnd, int from, int size) {
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);
            if (!end.isAfter(start)) {
                throw new IncorrectRequestException("Event must be published");
            }
        }
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                0L, "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(formatter));
        statsClient.saveEndpointHit(endpointHitDto);
        PageRequest page = checkPageableParameters(from, size);
        return eventRepository.findAll(page).toList().stream().sorted(Comparator.comparingInt(Event::getViews)).collect(Collectors.toList());
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

        Event event = eventRepository.save(
                EventMapper.convertToEvent(eventDto, initiator, category, location));

        log.info("Added New event: {}", event);
        return event;
    }


    @Override
    public List<Event> searchEvents(String users, String states, String categories, String rangeStart, String rangeEnd, int from, int size) {
        PageRequest page = checkPageableParameters(from, size);
        List<String> statesList = parseStates(states);
        List<Long> usersList = parseUsersIds(users);
        List<Long> categoriesList = parseCategoriesIds(categories);
        log.info("statesList = {}", statesList);
        log.info("usersList = {}", usersList);
        log.info("categoriesList = {}", categoriesList);
        if (users == null && states == null && categories == null) {
            return eventRepository.findAll(page).toList();
        }

        List<Event> events = eventRepository.searchEventsByAdmin(statesList, usersList, categoriesList, page);

        //   events.forEach(e -> e.setConfirmedRequests(requestRepository.getRequestCount(e.getId())));

        return events;
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

        log.info(" response = {}", response.getBody());

        String[] arrayStr = Objects.requireNonNull(response.getBody()).toString().split("=");
        String[] arrayNext = arrayStr[1].split(",");
        int hits = Integer.parseInt(arrayNext[0]);

        event.setViews(hits);
        return eventRepository.save(event);
    }


    private void checkExists(long eventId) {
        if (!eventRepository.existsById(eventId)
                || eventRepository.findById(eventId).get().getState().equals(State.PENDING)
                || eventRepository.findById(eventId).get().getState().equals(State.CANCELED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    private void saveStats(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                0L, "main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(formatter));
        statsClient.saveEndpointHit(endpointHitDto);
    }


    @Override
    public Event patchEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event oldEvent = eventRepository.findEventById(eventId);

        String state = oldEvent.getState().toString();
        String stateAction = updateEventAdminRequest.getStateAction();

        if (state.equals(State.CANCELED.toString())) {
            throw new ConflictException("Event with id " + eventId + " already " + state);
        }


        if (state.equals(State.PENDING.toString()) || !state.equals(State.PUBLISHED.toString())) {

            if (updateEventAdminRequest.getAnnotation() != null) {
                oldEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
            }
            if (updateEventAdminRequest.getCategoryId() != null) {
                Category category = categoryRepository.findById(updateEventAdminRequest.getCategoryId()).get();
                oldEvent.setCategory(category);
            }
            if (updateEventAdminRequest.getDescription() != null) {
                oldEvent.setDescription(updateEventAdminRequest.getDescription());
            }
            if (updateEventAdminRequest.getEventDate() != null) {
                LocalDateTime eventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), formatter);
                LocalDateTime currentDate = LocalDateTime.now();
                if (eventDate.isBefore(currentDate)) {
                    throw new NotValidEventDateException("date cannot be in the past");
                }
                oldEvent.setEventDate(updateEventAdminRequest.getEventDate());
            }
            if (updateEventAdminRequest.getLocation() != null) {
                Location location = locationRepository.save(new Location(0L, updateEventAdminRequest.getLocation().getLat(), updateEventAdminRequest.getLocation().getLon()));
                oldEvent.setLocation(location);
            }
            if (updateEventAdminRequest.getPaid() != null) {
                oldEvent.setPaid(updateEventAdminRequest.getPaid());
            }
            if (updateEventAdminRequest.getParticipantLimit() != null) {
                oldEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
            }
            if (updateEventAdminRequest.getRequestModeration() != null) {
                oldEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
            }
            if (updateEventAdminRequest.getTitle() != null) {
                oldEvent.setTitle(updateEventAdminRequest.getTitle());
            }

            if (stateAction != null && stateAction.equals(StateAction.PUBLISH_EVENT.toString())) {
                oldEvent.setState(State.PUBLISHED);
            }

            if (stateAction != null && stateAction.equals(StateAction.REJECT_EVENT.toString())) {
                oldEvent.setState(State.CANCELED);
            }


        } else {
            throw new ConflictException("Event with id " + eventId + " already " + state);
        }

        Event pachedEvent = eventRepository.save(oldEvent);
        log.info("Patched event: {}", pachedEvent);
        return pachedEvent;
    }


    @Override
    public Event patchEventByUser(long userId, long eventId, UpdateEventUserRequest eventRequest) {
        Event oldEvent = eventRepository.findEventById(eventId);
        String oldEventState = oldEvent.getState().toString();
        String stateAction = eventRequest.getStateAction();

        if (oldEventState.equals(State.CANCELED.toString()) || oldEventState.equals(State.PENDING.toString())) {


            if (eventRequest.getAnnotation() != null) {
                oldEvent.setAnnotation(eventRequest.getAnnotation());
            }
            if (eventRequest.getCategoryId() != null) {
                Category category = categoryRepository.findById(eventRequest.getCategoryId()).get();
                oldEvent.setCategory(category);
            }
            if (eventRequest.getDescription() != null) {
                oldEvent.setDescription(eventRequest.getDescription());
            }
            if (eventRequest.getEventDate() != null) {
                LocalDateTime eventDate = LocalDateTime.parse(eventRequest.getEventDate(), formatter);
                LocalDateTime currentDate = LocalDateTime.now();
                if (eventDate.isBefore(currentDate)) {
                    throw new NotValidEventDateException("date cannot be in the past");
                }
                oldEvent.setEventDate(eventRequest.getEventDate());
            }
            if (eventRequest.getLocation() != null) {
                Location location = locationRepository.save(new Location(0L, eventRequest.getLocation().getLat(), eventRequest.getLocation().getLon()));
                oldEvent.setLocation(location);
            }
            if (eventRequest.getPaid() != null) {
                oldEvent.setPaid(eventRequest.getPaid());
            }
            if (eventRequest.getParticipantLimit() != null) {
                oldEvent.setParticipantLimit(eventRequest.getParticipantLimit());
            }
            if (eventRequest.getRequestModeration() != null) {
                oldEvent.setRequestModeration(eventRequest.getRequestModeration());
            }
            if (eventRequest.getTitle() != null) {
                oldEvent.setTitle(eventRequest.getTitle());
            }
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

        Event pachedEvent = eventRepository.save(oldEvent);
        log.info("Patched event: {}", pachedEvent);
        return pachedEvent;
    }


    private PageRequest checkPageableParameters(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new RuntimeException("Параметр from не должен быть меньше 1");
        }
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }


    @Override
    public List<ParticipationRequest> getAllRequests() {
        return requestRepository.findAll();
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
}
