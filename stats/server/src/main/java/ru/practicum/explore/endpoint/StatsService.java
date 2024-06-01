package ru.practicum.explore.endpoint;

import endpoint.EndpointHitDto;
import ru.practicum.explore.viewstats.ViewStats;

import java.util.List;

public interface StatsService {

    EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats(String start, String end, String uris, Boolean unique);

}
