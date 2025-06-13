package com.kca_2sem_project.digitalob.auditlogs;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    @Scheduled(cron = "0 0 1 * * ?") // Runs at 1 AM every day
    public void cleanupOldLogs() {
        LocalDateTime fourMonthsAgo = LocalDateTime.now().minusMonths(4);
        logRepository.deleteByTimestampBefore(fourMonthsAgo);
    }

    public void logAction(String user, String action, String details) {
        Log log = Log.builder()
                .user(user)
                .action(action)
                .details(details)
                .build();
        logRepository.save(log);
    }

    public List<Log> getLogsWithFilters(String username, String action, LocalDate date) {
        return logRepository.findLogsWithFilters(username, action, date);
    }

    public List<Log> getLogsForUser(String user) {
        return logRepository.findByUserOrderByTimestampDesc(user);
    }
    public List<Log> getAllLogs() {
        return logRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }

    public List<Log> getLogsByAction(String action) {
        return logRepository.findByActionOrderByTimestampDesc(action);
    }
}
