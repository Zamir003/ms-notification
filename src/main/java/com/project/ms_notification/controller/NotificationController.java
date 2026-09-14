package com.project.ms_notification.controller;

import com.project.ms_notification.model.Notification;
import com.project.ms_notification.model.RecordStatus;
import com.project.ms_notification.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public record PageResponse<T>(List<T> items, int page, int size, long totalElements, int totalPages) {}

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<Notification> list(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        var p = notificationRepository.findAllByRecordStatusNot(RecordStatus.DELETED, PageRequest.of(page, size));
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
}