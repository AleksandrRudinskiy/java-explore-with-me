package ru.practicum.explore.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.explore.location.LocationDto;

public class EventShortDto {
    private long id;
    private String description;
    private String annotation;
    @JsonProperty("category")
    private Long categoryId;
    private String eventDate;
    private LocationDto location;

    private int confirmedRequests;

}
