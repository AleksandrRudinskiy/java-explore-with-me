package ru.practicum.explore.compilation.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "compilations_events", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationsEvents {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long compilationId;
    private long eventId;
}
