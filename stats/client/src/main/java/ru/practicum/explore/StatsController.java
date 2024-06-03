package ru.practicum.explore;

import endpoint.EndpointHitDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<Object> getStats(@RequestParam @NonNull String start,
                                           @RequestParam @NonNull String end,
                                           @RequestParam(defaultValue = "%events%") @NonNull String uris,
                                           @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("GET stats with parameters start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return statsClient.getStats(start, end, uris, unique);
    }

}
