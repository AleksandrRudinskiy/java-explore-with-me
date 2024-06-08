package ru.practicum.explore.compilation;

import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.CompilationFullDto;
import ru.practicum.explore.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getEventsCompilations(Boolean pinned, int from, int size);

    CompilationFullDto getEventCompilationById(Long compId);

    CompilationFullDto addCompilation(CompilationDto compilationDto);

    CompilationFullDto patchCompilation(UpdateCompilationRequest compilationDto, long compId);

    void deleteCompilationById(long compId);


}
