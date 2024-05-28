package ru.practicum;

import endpoint.EndpointHitDto;

import java.util.List;

public interface StatsService {

    EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats();
}
