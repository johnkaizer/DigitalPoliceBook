package com.kca_2sem_project.digitalob.auditlogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByUserOrderByTimestampDesc(String user);
    List<Log> findByActionOrderByTimestampDesc(String action);
    void deleteByTimestampBefore(LocalDateTime date);

    @Query("SELECT l FROM Log l WHERE " +
            "(:username IS NULL OR l.user = :username) AND " +
            "(:action IS NULL OR l.action = :action) AND " +
            "(:date IS NULL OR DATE(l.timestamp) = DATE(:date)) " +
            "ORDER BY l.timestamp DESC")
    List<Log> findLogsWithFilters(
            @Param("username") String username,
            @Param("action") String action,
            @Param("date") LocalDate date
    );
}
