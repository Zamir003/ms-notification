package com.project.ms_notification.dto;

public record BirthdayEvent(
        String personType,
        String fullName,
        String email
) {}