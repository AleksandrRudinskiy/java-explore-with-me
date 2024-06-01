package ru.practicum.explore.endpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.endpoint.model.EndpointHit;
import ru.practicum.explore.viewstats.ViewStats;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "select app, uri, count(*) as hits from endpoints " +
            "where uri like ?1 " +
            "and time_stamp between ?2 and ?3 " +
            "group by uri " +
            "order by hits desc", nativeQuery = true)
    List<ViewStats> getStats(String uri, String startDate, String endDate);

    @Query(value = "select app, uri, count(distinct ip) as hits from endpoints " +
            "where uri like ?1 " +
            "and time_stamp between ?2 and ?3 " +
            "group by uri " +
            "order by hits desc", nativeQuery = true)
    List<ViewStats> getStatsUniqueIp(String uri, String startDate, String endDate);

}
