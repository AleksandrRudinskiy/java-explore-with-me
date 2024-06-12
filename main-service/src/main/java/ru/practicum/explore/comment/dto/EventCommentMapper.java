package ru.practicum.explore.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.comment.model.EventComment;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.UserMapper;
import ru.practicum.explore.user.model.User;

@UtilityClass
public class EventCommentMapper {

    public static EventComment convertToEventComment(EventCommentDto eventCommentDto, User commenter, Event event) {
        return new EventComment(
                eventCommentDto.getId(),
                eventCommentDto.getText(),
                eventCommentDto.getCreated(),
                commenter,
                event,
                eventCommentDto.getState()
        );
    }

    public static EventCommentDto convertToEventCommentDto(EventComment eventComment) {
        return new EventCommentDto(
                eventComment.getId(),
                eventComment.getText(),
                eventComment.getCreated(),
                UserMapper.convertToUserShortDto(eventComment.getCommenter()),
                eventComment.getEvent().getId(),
                eventComment.getState()
        );
    }
}
