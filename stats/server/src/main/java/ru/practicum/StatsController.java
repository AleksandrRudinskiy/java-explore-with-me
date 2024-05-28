package ru.practicum;

import endpoint.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST-create endpoint to stat");
        return statsService.saveEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(required = false) String start,
                                    @RequestParam(required = false) String end) {
        log.info("GET stats with start {} and end {}", start, end);
        return statsService.getStats(start, end);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Object> getEventById(@PathVariable long id,
                                               HttpServletRequest request) {
        return statsService.getEventById(id, request);
    }

}
