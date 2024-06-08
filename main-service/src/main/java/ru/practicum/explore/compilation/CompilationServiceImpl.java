package ru.practicum.explore.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.common.NotFoundException;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.CompilationFullDto;
import ru.practicum.explore.compilation.dto.CompilationMapper;
import ru.practicum.explore.compilation.model.Compilation;
import ru.practicum.explore.compilation.model.CompilationEventInfo;
import ru.practicum.explore.compilation.model.CompilationsEvents;
import ru.practicum.explore.event.EventRepository;
import ru.practicum.explore.event.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationEventsRepository compilationEventsRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getEventsCompilations(Boolean pinned, int from, int size) {


        if (from < 0 || size <= 0) {
            throw new RuntimeException("Параметр from не должен быть меньше 1");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);


        List<Compilation> compilations = compilationRepository.findAll(page).toList();

        List<CompilationDto> compilationDtos = new ArrayList<>();

        for (Compilation compilation : compilations) {
            List<Long> events = compilationEventsRepository.findCompilationEventsByCompilationId(compilation.getId()).stream().map(CompilationEventInfo::getEventId).collect(Collectors.toList());

            CompilationDto compilationDto = CompilationMapper.convertToCompilationDto(compilation, events);
            compilationDtos.add(compilationDto);
        }

        return compilationDtos;
    }

    @Override
    public CompilationDto getEventCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).get();

        log.info("ПОДОРКА СОБЫТИЙ по compId {}:   {}", compId, compilation);


        List<Long> eventsIds = compilationEventsRepository.findCompilationEventsByCompilationId(compId).stream()
                .map(CompilationEventInfo::getEventId)
                .collect(Collectors.toList());


        return CompilationMapper.convertToCompilationDto(compilation, eventsIds);
    }

    @Override
    public CompilationFullDto addCompilation(CompilationDto compilationDto) {

        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(false);
        }

        Compilation compilation = compilationRepository.save(CompilationMapper.convertToCompilation(compilationDto));


        List<Long> eventsIds = new ArrayList<>();

        List<Event> events = new ArrayList<>();

        if (compilationDto.getEvents() != null) {
            eventsIds = compilationDto.getEvents();

            for (Long eventsId : eventsIds) {
                compilationEventsRepository.save(new CompilationsEvents(0L, compilation.getId(), eventsId));
                Event event = eventRepository.findEventById(eventsId);
                events.add(event);
            }

        }
        compilationDto.setId(compilation.getId());

        if (compilationDto.getEvents() == null) {
            compilationDto.setEvents(new ArrayList<>());
        }

        CompilationFullDto compilationFullDto = CompilationMapper.convertToFullDto(compilationDto, events);

        log.info("Added compilation full dto: {}", compilationFullDto);
        return compilationFullDto;
    }


    @Override
    public CompilationDto patchCompilation(CompilationDto compilationDto, long compId) {


        Compilation compilation = compilationRepository.findById(compId).get();
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        Compilation patchedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.convertToCompilationDto(patchedCompilation, compilationDto.getEvents());
    }

    @Override
    public void deleteCompilationById(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id " + compId + " not found.");
        }
        compilationRepository.deleteById(compId);
    }
}
