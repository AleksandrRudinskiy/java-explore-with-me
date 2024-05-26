package ru.practicum.endpoint;

import endpoints.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.viewstats.ViewStats;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {
    //  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto) {
        endpointHitDto.setTimeStamp(LocalDateTime.now().toString());
        return EndpointHitMapper.convertToEndpointHitDto(
                statsRepository.save(EndpointHitMapper.convertDtoToEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStats> getStats() {
        return statsRepository.getStats();
    }
}
