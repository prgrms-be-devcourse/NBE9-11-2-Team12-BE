package com.rungo.api.domain.registration.event;

public record RegistrationCompletedEvent(String email, String marathonTitle, String courseName) {}
