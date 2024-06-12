package ru.practicum.explore.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.category.CategoryMapper;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.common.State;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.location.Location;
import ru.practicum.explore.location.LocationMapper;
import ru.practicum.explore.user.UserMapper;
import ru.practicum.explore.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventDto convertToEventDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory().getId(),
                event.getDescription(),
                event.getEventDate(),
                LocationMapper.convertToLocationDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getTitle()
        );
    }

    public static Event convertToEvent(EventDto eventDto, User initiator, Category category, Location location) {
        return new Event(
                eventDto.getId(),
                eventDto.getAnnotation(),
                category,
                0,
                LocalDateTime.now().format(formatter),
                eventDto.getDescription(),
                eventDto.getEventDate(),
                initiator,
                location,
                eventDto.getPaid(),
                eventDto.getParticipantLimit(),
                LocalDateTime.now().format(formatter),
                eventDto.getRequestModeration(),
                State.PENDING,
                eventDto.getTitle(),
                0L
        );
    }

    public static EventFullDto convertToEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.convertToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                UserMapper.convertToUserShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );
    }

    public EventShortDto convertToEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getDescription(),
                event.getAnnotation(),
                CategoryMapper.convertToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                UserMapper.convertToUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }
}
