package ru.practicum;

import endpoint.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto saveEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST-create endpoint to stat");
        return statsService.saveEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats() {
        log.info("GET stats");
        return statsService.getStats();
    }

}
