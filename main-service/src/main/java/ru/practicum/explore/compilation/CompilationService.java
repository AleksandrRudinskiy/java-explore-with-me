package ru.practicum.explore.compilation;

import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.CompilationFullDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getEventsCompilations(Boolean pinned, int from, int size);

    CompilationDto getEventCompilationById(Long compId);

    CompilationFullDto addCompilation(CompilationDto compilationDto);

    CompilationDto patchCompilation(CompilationDto compilationDto, long compId);

    void deleteCompilationById(long compId);


}
