package ru.practicum.explore.comment;

import ru.practicum.explore.comment.dto.EventCommentDto;

import java.util.List;

public interface EventCommentService {

    EventCommentDto addComment(long commenterId, long eventId, EventCommentDto eventCommentDto);

    EventCommentDto patchCommentByUser(long commentId, EventCommentDto eventCommentDto);

    EventCommentDto patchCommentStateByAdmin(long commentId, EventCommentDto eventCommentDto);

    EventCommentDto getCommentById(long commentId);

    List<EventCommentDto> getEventComments(long eventId, String state, String start, String end, int from, int size);

    void deleteComment(long commentId);
}
