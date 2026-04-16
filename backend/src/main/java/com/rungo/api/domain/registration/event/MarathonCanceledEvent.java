package com.rungo.api.domain.registration.event;

import java.util.List;

public record MarathonCanceledEvent(String marathonTitle, List<String> participantEmails) {}