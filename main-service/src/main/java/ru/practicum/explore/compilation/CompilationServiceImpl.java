package ru.practicum.explore.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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
        Compilation compilation = null;
        if (compilationRepository.findById(compId).isPresent()) {
            compilation = compilationRepository.findById(compId).get();
        }

        List<Long> eventsIds = compilationEventsRepository.findCompilationEventsByCompilationId(compId).stream()
                .map(CompilationEventInfo::getEventId)
                .collect(Collectors.toList());


        assert compilation != null;
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


        return CompilationMapper.convertToFullDto(compilationDto, events);
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
}
