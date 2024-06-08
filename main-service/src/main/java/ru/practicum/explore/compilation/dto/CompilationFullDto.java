package ru.practicum.explore.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.event.model.Event;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationFullDto {
    private long id;
    private Boolean pinned;
    private String title;
    private List<Event> events;
}
