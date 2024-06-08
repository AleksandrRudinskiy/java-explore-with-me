package ru.practicum.explore.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users where id in (:ids)", nativeQuery = true)
    List<User> selectUsers(@Param("ids") List<Long> ids, Pageable page);


}
