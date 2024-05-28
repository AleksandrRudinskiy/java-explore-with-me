package ru.practicum;

import endpoint.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto) {
        endpointHitDto.setTimeStamp(LocalDateTime.now().toString());
        return EndpointHitMapper.convertToEndpointHitDto(
                statsRepository.save(EndpointHitMapper.convertDtoToEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStats> getStats(String start, String end) {
        if (start == null || end == null) {
            throw new NotCorrectDataException("Даты не заданы!");
        }
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);

        if (endDate.isBefore(startDate)) {
            throw new NotCorrectDataException("Неверно заданы даты!");
        }
        return statsRepository.getStats();
    }

    @Override
    public ResponseEntity<Object> getEventById(long id, HttpServletRequest request) {
        EndpointHit endpointHit = statsRepository.findByUri(request.getRequestURI());
        statsRepository.save(new EndpointHit(
                0L, endpointHit.getApp(), request.getRequestURI(), endpointHit.getIp(), LocalDateTime.now()));
        System.out.println(endpointHit);
        return null;
    }
}
