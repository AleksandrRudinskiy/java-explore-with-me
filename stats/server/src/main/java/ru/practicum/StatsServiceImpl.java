package ru.practicum;

import endpoint.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto) {
        endpointHitDto.setTimeStamp(LocalDateTime.now().toString());
        EndpointHitDto hitDto = EndpointHitMapper.convertToEndpointHitDto(
                statsRepository.save(EndpointHitMapper.convertDtoToEndpointHit(endpointHitDto)));
        log.info("Posted endpointHit {}", hitDto);
        return hitDto;
    }

    @Override
    public List<ViewStats> getStats(String start, String end, String uris, Boolean unique) {
        if (start == null || end == null) {
            throw new NotCorrectDataException("Даты не заданы!");
        }
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        if (endDate.isBefore(startDate)) {
            throw new NotCorrectDataException("Неверно заданы даты!");
        }

        if (uris.equals("%events%")) {
            return statsRepository.getStats(uris);
        }

        String decodeUris = java.net.URLDecoder.decode(uris, StandardCharsets.UTF_8);
        String[] arrayUris = decodeUris.split(",");
        for (String s : arrayUris) {
            log.info("uris = : {}", s);
        }


        if (unique) {
            log.info("СТАТИСТИКА:");
            List<ViewStats> stats = new ArrayList<>();
            for (String s : arrayUris) {
                stats.addAll(statsRepository.getStatsUniqueIp(s));
            }
            stats.forEach(x -> log.info("uri = {}, app = {}, hits = {}", x.getUri(), x.getApp(), x.getHits()));
            return stats.stream()
                    .sorted(Comparator.comparingInt(ViewStats::getHits).reversed()).collect(Collectors.toList());
        } else {
            log.info("СТАТИСТИКА:");
            List<ViewStats> stats = new ArrayList<>();
            for (String s : arrayUris) {
                stats.addAll(statsRepository.getStats(s));
            }
            stats.forEach(x -> log.info("uri = {}, app = {}, hits = {}", x.getUri(), x.getApp(), x.getHits()));
            return stats.stream()
                    .sorted(Comparator.comparingInt(ViewStats::getHits).reversed()).collect(Collectors.toList());
        }
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
