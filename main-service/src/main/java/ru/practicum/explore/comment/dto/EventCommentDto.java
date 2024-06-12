package ru.practicum.explore.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.common.State;
import ru.practicum.explore.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCommentDto {
    private long id;
    @NotNull
    @NotBlank
    private String text;
    private String created;
    private UserShortDto commenter;
    private long eventId;
    private State state;
}
