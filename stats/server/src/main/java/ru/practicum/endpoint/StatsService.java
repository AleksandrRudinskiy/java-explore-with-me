package ru.practicum.endpoint;

import endpoints.EndpointHitDto;
import ru.practicum.viewstats.ViewStats;

import java.util.List;

public interface StatsService {

    EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats();
}
