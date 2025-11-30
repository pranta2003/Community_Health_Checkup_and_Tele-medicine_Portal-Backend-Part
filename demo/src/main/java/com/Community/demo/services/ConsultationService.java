package com.Community.demo.services;

import com.Community.demo.model.ConsultationSession;

import java.util.List;
import java.util.Optional;

public interface ConsultationService {
    ConsultationSession createSession(ConsultationSession session);
    Optional<ConsultationSession> getSessionById(Long id);
    List<ConsultationSession> getAllSessions();
    List<ConsultationSession> getUpcomingSessions();
    Optional<ConsultationSession> startSession(Long id);
    Optional<ConsultationSession> endSession(Long id);
    Optional<ConsultationSession> updateSession(ConsultationSession session);
    boolean deleteSession(Long id);
}
