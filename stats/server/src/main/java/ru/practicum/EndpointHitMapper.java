package ru.practicum;

import endpoint.EndpointHitDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EndpointHitMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public static EndpointHit convertDtoToEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                LocalDateTime.parse(endpointHitDto.getTimeStamp(), formatter)
        );
    }

    public static EndpointHitDto convertToEndpointHitDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimeStamp().format(formatter)
        );
    }
}
