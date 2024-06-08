package ru.practicum.explore.participation_request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query(value = "select count(*) as hits from requests " +
            "where event_id = ?1 ", nativeQuery = true)
    int getRequestCount(long eventId);

}
