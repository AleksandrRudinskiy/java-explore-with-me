package ru.practicum.explore.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.compilation.model.Compilation;
import ru.practicum.explore.event.model.Event;

import java.util.List;

@UtilityClass
public class CompilationMapper {

    public static Compilation convertToCompilation(CompilationDto compilationDto) {
        return new Compilation(
                compilationDto.getId(),
                compilationDto.getPinned(),
                compilationDto.getTitle()
        );
    }

    public static CompilationDto convertToCompilationDto(Compilation compilation, List<Long> eventsIds) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle(),
                eventsIds
        );
    }

    public static CompilationFullDto convertToFullDto(CompilationDto compilationDto, List<Event> events) {
        return new CompilationFullDto(
                compilationDto.getId(),
                compilationDto.getPinned(),
                compilationDto.getTitle(),
                events
        );
    }
}
