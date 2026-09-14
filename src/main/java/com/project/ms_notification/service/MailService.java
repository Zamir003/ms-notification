package com.project.ms_notification.service;

import com.project.ms_notification.model.Notification;
import com.project.ms_notification.model.NotificationStatus;
import com.project.ms_notification.model.NotificationType;
import com.project.ms_notification.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendAndStore(NotificationType type, String to, String subject, String body) {
        Notification n = new Notification();
        n.setType(type);
        n.setToEmail(to);
        n.setSubject(subject);
        n.setBody(body);

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);

            n.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            n.setStatus(NotificationStatus.FAILED);
        }

        notificationRepository.save(n);
    }
}