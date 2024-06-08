package ru.practicum.explore.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.event.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findEventsByInitiatorId(long initiatorId, Pageable page);

//    @Query(value = "select * from events where initiator_id in (:userIds)", nativeQuery = true)
//    List<Event> searchEvents(@Param("userIds") List<Long> userIds, Pageable page);

    @Query(value = "select * from events where initiator_id = ?1", nativeQuery = true)
    List<Event> searchEvents(long userId, Pageable page);

    Event findEventById(long id);

    @Query(value = "select * from events where " +
            "state in (:states) and " +
            "initiator_id in (:users) and " +
            "category_id in (:category) ", nativeQuery = true)
    List<Event> searchEventsByAdmin(@Param("states") List<String> states,
                                    @Param("users") List<Long> users,
                                    @Param("category") List<Long> category,
                                    Pageable page);

    @Query(value = "select * from events where state like '%:state'", nativeQuery = true)
    List<Event> findByState(String state);


}
