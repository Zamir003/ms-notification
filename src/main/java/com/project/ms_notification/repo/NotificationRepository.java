package com.project.ms_notification.repo;

import com.project.ms_notification.model.Notification;
import com.project.ms_notification.model.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByRecordStatusNot(RecordStatus status, Pageable pageable);
}