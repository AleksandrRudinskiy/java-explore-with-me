package ru.practicum.explore.event;

import endpoint.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.endpoint.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;


    @Override
    public List<Event> getEvents(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                0L, "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().toString());
        statsClient.saveEndpointHit(endpointHitDto);
        return eventRepository.findAll();
    }
}
