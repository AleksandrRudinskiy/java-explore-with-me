package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.endpoint.EndpointHitDto;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST endpoint");
        return statsClient.saveEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats() {
        log.info("GET stats");
        return statsClient.getStats();
    }

}
