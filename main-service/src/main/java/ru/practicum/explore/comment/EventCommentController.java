package ru.practicum.explore.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.comment.dto.EventCommentDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventCommentController {
    private final EventCommentService commentService;

    @PostMapping("/event/{eventId}/user/{commenterId}/comments")
    public EventCommentDto addComment(@PathVariable long eventId,
                                      @PathVariable long commenterId,
                                      @RequestBody @Valid EventCommentDto eventCommentDto) {
        log.info("POST /event/{}/user/{}/comments", eventId, commenterId);
        return commentService.addComment(commenterId, eventId, eventCommentDto);
    }

    @PatchMapping("/comments/{commentId}")
    public EventCommentDto patchCommentByUser(@PathVariable long commentId,
                                              @RequestBody @Valid EventCommentDto eventCommentDto) {
        log.info("PATCH /comments/{}", commentId);
        return commentService.patchCommentByUser(commentId, eventCommentDto);
    }

    @PatchMapping("/comments/{commentId}/admin")
    public EventCommentDto patchCommentStateByAdmin(@PathVariable long commentId,
                                                    @RequestBody @Valid EventCommentDto eventCommentDto) {
        log.info("PATCH /comments/{}", commentId);
        return commentService.patchCommentStateByAdmin(commentId, eventCommentDto);
    }

    @GetMapping("/comments/{commentId}")
    public EventCommentDto getCommentById(@PathVariable long commentId) {
        log.info("GET /comments/{}", commentId);
        return commentService.getCommentById(commentId);
    }

    @GetMapping("/event/{eventId}/comments")
    public List<EventCommentDto> getEventComments(@PathVariable long eventId,
                                                  @RequestParam(defaultValue = "PUBLISHED") String state,
                                                  @RequestParam(defaultValue = "2020-01-01 00:00:00") String start,
                                                  @RequestParam(defaultValue = "2035-01-01 00:00:00") String end,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("GET /event/{}/comments", eventId);
        return commentService.getEventComments(eventId, state, start, end, from, size);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable long commentId) {
        log.info("DELETE /comments/{}", commentId);
        commentService.deleteComment(commentId);
    }

}
