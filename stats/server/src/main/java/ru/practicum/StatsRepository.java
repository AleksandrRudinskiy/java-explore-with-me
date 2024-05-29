package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "select app, uri, count(*) as hits from endpoints " +
            "where uri like ?1 " +
            "group by uri " +
            "order by hits desc", nativeQuery = true)
    List<ViewStats> getStats(String uri);

    @Query(value = "select app, uri, count(distinct ip) as hits from endpoints " +
            "where uri like ?1 " +
            "group by uri " +
            "order by hits desc", nativeQuery = true)
    List<ViewStats> getStatsUniqueIp(String uri);


    EndpointHit findByUri(String uri);

}
