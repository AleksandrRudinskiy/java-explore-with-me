package ru.practicum.explore.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.compilation.model.CompilationEventInfo;
import ru.practicum.explore.compilation.model.CompilationsEvents;

import java.util.List;

public interface CompilationEventsRepository extends JpaRepository<CompilationsEvents, Long> {

    List<CompilationEventInfo> findCompilationEventsByCompilationId(long compId);
}
