package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "select app, uri, count(*) as hits from endpoints " +
            "group by app ", nativeQuery = true)
    List<ViewStats> getStats();


}
