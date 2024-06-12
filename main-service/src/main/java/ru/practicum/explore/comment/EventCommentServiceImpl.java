package ru.practicum.explore.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.comment.dto.EventCommentDto;
import ru.practicum.explore.comment.dto.EventCommentMapper;
import ru.practicum.explore.comment.model.EventComment;
import ru.practicum.explore.common.ConflictException;
import ru.practicum.explore.common.State;
import ru.practicum.explore.event.EventRepository;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.UserRepository;
import ru.practicum.explore.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventCommentRepository repository;

    @Override
    public EventCommentDto addComment(long commenterId, long eventId, EventCommentDto eventCommentDto) {
        eventCommentDto.setCreated(LocalDateTime.now().format(formatter));
        User commenter = userRepository.findById(commenterId).get();
        Event event = eventRepository.findById(eventId).get();
        EventComment eventComment = repository.save(
                EventCommentMapper.convertToEventComment(eventCommentDto, commenter, event));
        return EventCommentMapper.convertToEventCommentDto(eventComment);
    }

    @Override
    public EventCommentDto patchCommentByUser(long commentId, EventCommentDto eventCommentDto) {
        if (eventCommentDto.getState() != null) {
            throw new ConflictException("Обновить статус может только администратор.");
        }
        EventComment eventComment = repository.findById(commentId).get();
        String patchedText = eventCommentDto.getText();
        eventComment.setText(patchedText);
        return EventCommentMapper.convertToEventCommentDto(
                repository.save(eventComment));
    }

    @Override
    public EventCommentDto patchCommentStateByAdmin(long commentId, EventCommentDto eventCommentDto) {
        EventComment eventComment = repository.findById(commentId).get();
        State patchedStatus = eventCommentDto.getState();
        if (patchedStatus != null) {
            eventComment.setState(patchedStatus);
        }
        return EventCommentMapper.convertToEventCommentDto(
                repository.save(eventComment));
    }

    @Override
    public EventCommentDto getCommentById(long commentId) {
        return EventCommentMapper.convertToEventCommentDto(
                repository.findById(commentId).get());
    }

    @Override
    public List<EventCommentDto> getEventComments(long eventId, String state, String start, String end, int from, int size) {
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        return repository.findEventCommentsByEventId(eventId).stream()
                .filter(e -> LocalDateTime.parse(e.getCreated(), formatter).isAfter(startDate)
                        && LocalDateTime.parse(e.getCreated(), formatter).isBefore(endDate)
                        && e.getState().equals(State.valueOf(state)))
                .limit(size)
                .map(EventCommentMapper::convertToEventCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(long commentId) {
        repository.deleteById(commentId);
    }
}
