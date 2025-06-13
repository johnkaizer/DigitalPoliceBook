package com.kca_2sem_project.digitalob.auditlogs;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @GetMapping("/all")
    public ResponseEntity<List<Log>> getAllLogs() {
        List<Log> logs = logService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping
    public ResponseEntity<List<Log>> getLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Log> logs = logService.getLogsWithFilters(username, action, date);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Log>> getLogsForUser(@PathVariable String username) {
        List<Log> logs = logService.getLogsForUser(username);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<Log>> getLogsByAction(@PathVariable String action) {
        List<Log> logs = logService.getLogsByAction(action);
        return ResponseEntity.ok(logs);
    }
}