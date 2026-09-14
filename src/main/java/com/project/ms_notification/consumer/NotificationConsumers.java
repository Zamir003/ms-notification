package com.project.ms_notification.consumer;

import com.project.ms_notification.config.RabbitConfig;
import com.project.ms_notification.dto.BirthdayEvent;
import com.project.ms_notification.dto.PaymentEvent;
import com.project.ms_notification.model.NotificationType;
import com.project.ms_notification.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumers {

    private final MailService mailService;

    @Value("${app.alerts.dev-email}")
    private String devEmail;

    @RabbitListener(queues = RabbitConfig.PAYMENTS_QUEUE)
    public void onPayment(PaymentEvent event) {

        if (event.currency() == null || event.currency().isBlank()) {
            throw new IllegalArgumentException("Invalid currency");
        }

        String subject = "Payment " + event.status() + " (paymentId=" + event.paymentId() + ")";
        String body = """
                Enrollment: %d
                Amount: %s %s
                Status: %s
                """.formatted(event.enrollmentId(), event.amount(), event.currency(), event.status());

        mailService.sendAndStore(NotificationType.PAYMENT, event.payerEmail(), subject, body);
    }

    @RabbitListener(queues = RabbitConfig.BIRTHDAY_QUEUE)
    public void onBirthday(BirthdayEvent event) {
        String subject = "Happy Birthday, " + event.fullName() + "!";
        String body = "Dear " + event.fullName() + ",\n\nIdTech wishes you a happy birthday 🎉";

        mailService.sendAndStore(NotificationType.BIRTHDAY, event.email(), subject, body);
    }

    @RabbitListener(queues = RabbitConfig.PAYMENTS_DLQ)
    public void onPaymentsDlq(Object raw) {
        mailService.sendAndStore(NotificationType.DLQ_ALERT, devEmail,
                "DLQ ALERT: payments.events.dlq",
                "A message landed in payments DLQ:\n\n" + raw);
    }

    @RabbitListener(queues = RabbitConfig.BIRTHDAY_DLQ)
    public void onBirthdayDlq(Object raw) {
        mailService.sendAndStore(NotificationType.DLQ_ALERT, devEmail,
                "DLQ ALERT: birthday.events.dlq",
                "A message landed in birthday DLQ:\n\n" + raw);
    }
}