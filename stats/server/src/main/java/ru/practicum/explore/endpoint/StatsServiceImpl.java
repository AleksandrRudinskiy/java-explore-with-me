package ru.practicum.explore.endpoint;

import endpoint.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.common.NotCorrectDataException;
import ru.practicum.explore.endpoint.model.EndpointHitMapper;
import viewstats.ViewStats;

import javax.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

        endpointHitDto.setTimeStamp(LocalDateTime.now().format(formatter));
        return EndpointHitMapper.convertToEndpointHitDto(
                statsRepository.save(EndpointHitMapper.convertDtoToEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, String uris, Boolean unique) {

        log.info("STATS method getStats !!!!!!");
        log.info("start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);


        if (start == null || end == null) {
            throw new NotCorrectDataException("Даты не заданы!");
        }
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        if (endDate.isBefore(startDate)) {
            throw new NotCorrectDataException("Неверно заданы даты!");
        }

        if (uris.equals("%events%")) {
            return statsRepository.getStats(uris, start, end);
        }

        String decodeUris = URLDecoder.decode(uris, StandardCharsets.UTF_8);
        String[] arrayUris = decodeUris.split(",");

        if (unique) {
            List<ViewStats> stats = new ArrayList<>();
            Arrays.stream(arrayUris).forEach(s -> stats.addAll(statsRepository.getStatsUniqueIp(s, start, end)));
            return stats.stream()
                    .sorted(Comparator.comparingInt(ViewStats::getHits).reversed())
                    .collect(Collectors.toList());
        } else {
            List<ViewStats> stats = new ArrayList<>();
            Arrays.stream(arrayUris).forEach(s -> stats.addAll(statsRepository.getStats(s, start, end)));
            return stats.stream()
                    .sorted(Comparator.comparingInt(ViewStats::getHits).reversed())
                    .collect(Collectors.toList());
        }
    }

}
