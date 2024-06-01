package ru.practicum.explore.endpoint;

import endpoint.EndpointHitDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.viewstats.ViewStats;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveEndpointHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST-create endpoint to stat : {}", endpointHitDto);
        return statsService.saveEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @NonNull String start,
                                    @RequestParam @NonNull String end,
                                    @RequestParam(defaultValue = "%events%") @NonNull String uris,
                                    @RequestParam(required = false, defaultValue = "false") Boolean unique
    ) {
        log.info("GET stats with parameters: start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }

}
