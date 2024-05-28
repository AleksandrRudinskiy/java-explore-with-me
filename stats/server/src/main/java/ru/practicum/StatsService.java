package ru.practicum;

import endpoint.EndpointHitDto;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StatsService {

    EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats(String start, String end);

    ResponseEntity<Object> getEventById(long id, HttpServletRequest request);
}
