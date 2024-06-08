package ru.practicum.explore.compilation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.CompilationFullDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDto> getEventsCompilations(@RequestParam(required = false) Boolean pinned,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("GET /compilations, pinned = {}, from = {}, size = {}", pinned, from, size);
        return compilationService.getEventsCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getEventCompilationById(@PathVariable Long compId) {
        log.info("GET /compilations/{}", compId);
        return compilationService.getEventCompilationById(compId);
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationFullDto addCompilation(@RequestBody @Valid CompilationDto compilationDto) {
        log.info("POST /admin/compilations with body {}", compilationDto);
        return compilationService.addCompilation(compilationDto);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationFullDto patchCompilation(@RequestBody @Valid CompilationDto compilationDto,
                                               @PathVariable Long compId) {
        log.info("PATCH /admin/compilations/{} with body: {}", compId, compilationDto);
        return compilationService.patchCompilation(compilationDto, compId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/compilations/{compId}")
    public void deleteCompilationById(@PathVariable long compId) {
        log.info("DELETE /admin/compilations/{}", compId);
        compilationService.deleteCompilationById(compId);

    }


}
