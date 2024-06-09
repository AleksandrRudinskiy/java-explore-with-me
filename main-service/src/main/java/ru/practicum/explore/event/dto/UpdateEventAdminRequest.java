package ru.practicum.explore.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.explore.location.LocationDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest extends UpdateEventUserRequest {
    @Size(min = 20, message = "{validation.annotation.size.too_short}")
    @Size(max = 2000, message = "{validation.annotation.size.too_long}")
    private String annotation;
    @JsonProperty("category")
    private Long categoryId;
    @Size(max = 7000, message = "{validation.description.size.too_long}")
    @Size(min = 20, message = "{validation.description.size.too_short}")
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    @Min(0)
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 3, message = "{validation.title.size.too_short}")
    @Size(max = 120, message = "{validation.title.size.too_long}")
    private String title;

}
